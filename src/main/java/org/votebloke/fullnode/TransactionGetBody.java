package org.votebloke.fullnode;

import blockchain.Transaction;
import blockchain.TransactionOutput;

public class TransactionGetBody {
    String transactionId;
    String timeStamp;
    String entryType;
    String transactionAuthor;

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
}
