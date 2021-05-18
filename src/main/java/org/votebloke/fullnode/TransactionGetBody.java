package org.votebloke.fullnode;

import org.votebloke.blockchain.Transaction;
import org.votebloke.blockchain.TransactionOutput;

import java.util.HashMap;

public class TransactionGetBody implements java.io.Serializable {
  public String transactionId;
  public String timeStamp;
  public String entryType;
  public String transactionAuthor;
  public HashMap<String, String[]> entryMetadata;
  public String dataToSign;

  TransactionGetBody(Transaction transaction) {
    this.transactionId = transaction.getId();
    this.timeStamp = transaction.getTimeStamp();
    this.entryType = transaction.getEntryType();
    this.transactionAuthor = transaction.getSigner();
    this.entryMetadata = transaction.data.getMetadata();
    this.dataToSign = transaction.getSignData();
  }

  TransactionGetBody(TransactionOutput transactionOutput) {
    this.transactionId = transactionOutput.getParentTransactionId();
    this.timeStamp = transactionOutput.getData().getTimeStamp().toString();
    this.entryType = transactionOutput.getData().getEntryType();
    this.transactionAuthor = transactionOutput.getData().getAuthor();
    this.entryMetadata = transactionOutput.getData().getMetadata();
    this.dataToSign = null;
  }

  TransactionGetBody(
      String transactionId,
      String timeStamp,
      String entryType,
      String transactionAuthor,
      HashMap<String, String[]> metadata,
      String dataToSign) {
    this.transactionId = transactionId;
    this.timeStamp = timeStamp;
    this.entryType = entryType;
    this.transactionAuthor = transactionAuthor;
    this.entryMetadata = metadata;
    this.dataToSign = dataToSign;
  }
}
