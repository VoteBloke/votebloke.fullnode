package org.votebloke.fullnode;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.votebloke.blockchain.Account;
import org.votebloke.blockchain.Block;
import org.votebloke.blockchain.Chain;
import org.votebloke.blockchain.Elections;
import org.votebloke.blockchain.Transaction;
import org.votebloke.blockchain.TransactionInput;
import org.votebloke.blockchain.TransactionOutput;
import org.votebloke.blockchain.Vote;

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

  public void createAccount(String base64Key) {
    try {
      this.account = new Account(base64Key);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
  }

  public Transaction callElections(String question, String[] answers) {
    if (getAccount() != null) {
      Transaction elections = getAccount().createElections(question, answers);
      latestBlock.addTransaction(elections);
      return elections;
    }
    return null;
  }

  public Transaction vote(String answer, String electionsTransactionId) {
    if (getAccount() != null) {
      TransactionOutput electionsOut =
          latestBlock.getUnconsumedOutputs().stream()
              .filter(output -> electionsTransactionId.equals(output.getParentTransactionId()))
              .findAny()
              .orElse(null);
      Transaction voteTransaction =
          getAccount()
              .vote(
                  answer,
                  (Elections) electionsOut.getData(),
                  new ArrayList<>(List.of(new TransactionInput(electionsOut))));
      latestBlock.addTransaction(voteTransaction);
      return voteTransaction;
    }
    return null;
  }

  public ArrayList<TransactionGetBody> getAllOutputTransactions() {
    ArrayList<TransactionGetBody> transactionsResponses = new ArrayList<>();
    for (TransactionOutput transactionOutput : latestBlock.getUnconsumedOutputs()) {
      transactionsResponses.add(new TransactionGetBody(transactionOutput));
    }

    return transactionsResponses;
  }

  public Transaction tallyElections(String electionsTransactionId) {
    if (getAccount() == null) return null;

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

    Transaction tallyTransaction = getAccount().tally(tallyInputEntries);
    latestBlock.addTransaction(tallyTransaction);

    return tallyTransaction;
  }

  public ArrayList<TransactionGetBody> getOpenElections() {
    ArrayList<TransactionGetBody> transactionsResponses = new ArrayList<>();
    for (TransactionOutput transactionOutput : latestBlock.getOpenElections()) {
      transactionsResponses.add(new TransactionGetBody(transactionOutput));
    }

    return transactionsResponses;
  }

  public ArrayList<TransactionGetBody> getUnsignedTransactions(String keyId) {
    ArrayList<TransactionGetBody> transactionsResponses = new ArrayList<>();
    for (Transaction transaction : latestBlock.getUnsignedTransactions(keyId)) {
      transactionsResponses.add(new TransactionGetBody(transaction));
    }
    return transactionsResponses;
  }

  public Account getAccount() {
    return account;
  }

  public void signTransaction(String transactionId, String signature) {
    latestBlock.signTransaction(transactionId, signature.getBytes(StandardCharsets.UTF_8));
  }
}
