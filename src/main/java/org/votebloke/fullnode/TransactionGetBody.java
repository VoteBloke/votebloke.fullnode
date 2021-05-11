package org.votebloke.fullnode;

import blockchain.Transaction;
import blockchain.TransactionOutput;

public class TransactionGetBody implements java.io.Serializable {
    public String transactionId;
    public String timeStamp;
    public String entryType;
    public String transactionAuthor;

    TransactionGetBody(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.timeStamp = transaction.getTimeStamp();
        this.entryType = transaction.getEntryType();
        this.transactionAuthor = transaction.getSigner();
    }

    TransactionGetBody(TransactionOutput transactionOutput) {
        this.transactionId = transactionOutput.getParentTransactionId();
        this.timeStamp = transactionOutput.getData().getTimeStamp().toString();
        this.entryType = transactionOutput.getData().getEntryType();
        this.transactionAuthor = transactionOutput.getData().getAuthor();
    }

    TransactionGetBody(String transactionId, String timeStamp, String entryType, String transactionAuthor) {
        this.transactionId = transactionId;
        this.timeStamp = timeStamp;
        this.entryType = entryType;
        this.transactionAuthor = transactionAuthor;
    }
}
