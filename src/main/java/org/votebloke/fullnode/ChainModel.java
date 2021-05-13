package org.votebloke.fullnode;

import blockchain.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class BlockchainModel {
  private Chain blockchain = null;
  private Block latestBlock = null;
  private Account account = null;

  BlockchainModel() {
    Block genesisBlock = new Block("0", "v1", 0, null);
    genesisBlock.mineHash();
    this.blockchain = new Chain(genesisBlock);

    Block newBlock =
        new Block(blockchain.getLatestBlockHash(), "v1", 2, new ArrayList<TransactionOutput>());
    blockchain.addBlock(newBlock);
    latestBlock = newBlock;
  }

  public void createAccount() {
    try {
      account = Account.createAccount();
    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
  }

  public void callElections(String question, String[] answers) {
    if (account != null) {
      latestBlock.addTransaction(account.createElections(question, answers));
    }
  }

  public void vote(String answer, String electionsTransactionId) {
    if (account != null) {
      TransactionOutput electionsOut =
          latestBlock.getUnconsumedOutputs().stream()
              .filter(output -> electionsTransactionId.equals(output.getParentTransactionId()))
              .findAny()
              .orElse(null);
      Transaction voteTransaction =
          account.vote(
              answer,
              (Elections) electionsOut.getData(),
              new ArrayList<>(List.of(new TransactionInput(electionsOut))));
      latestBlock.addTransaction(voteTransaction);
    }
  }

  public ArrayList<TransactionGetBody> getAllOutputTransactions() {
    ArrayList<TransactionGetBody> transactionsResponses = new ArrayList<>();
    for (TransactionOutput transactionOutput : latestBlock.getUnconsumedOutputs()) {
      transactionsResponses.add(new TransactionGetBody(transactionOutput));
    }

    return transactionsResponses;
  }

  public void tallyElections(String electionsTransactionId) {
    if (account == null) return;

    ArrayList<TransactionInput> tallyInputEntries = new ArrayList<>();
    tallyInputEntries.add(
        new TransactionInput(
            latestBlock.getUnconsumedOutputs().stream()
                .filter(output -> electionsTransactionId.equals(output.getParentTransactionId()))
                .findAny()
                .orElse(null)));
    List<TransactionOutput> voteTransactions =
        latestBlock.getUnconsumedOutputs().stream()
            .filter(
                output -> {
                  try {
                    String electionsId =
                        tallyInputEntries.get(0).getTransactionOut().getData().getId();
                    Vote vote = (Vote) output.getData();
                    return electionsId.equals(vote.getElectionsId());
                  } catch (Exception ignored) {
                    return false;
                  }
                })
            .collect(Collectors.toList());

    for (TransactionOutput output : voteTransactions) {
      tallyInputEntries.add(new TransactionInput(output));
    }

    Transaction tallyTransaction = account.tally(tallyInputEntries);
    latestBlock.addTransaction(tallyTransaction);
  }

  public ArrayList<TransactionGetBody> getOpenElections() {
    ArrayList<TransactionGetBody> transactionsResponses = new ArrayList<>();
    for (TransactionOutput transactionOutput : latestBlock.getOpenElections()) {
      transactionsResponses.add(new TransactionGetBody(transactionOutput));
    }

    return transactionsResponses;
  }
}
