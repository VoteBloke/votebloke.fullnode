package org.votebloke.fullnode;

import java.io.Serializable;

class ElectionsPostBody implements Serializable {
  public String elections;
  public String[] answers;

  ElectionsPostBody(String elections, String[] answers) {
    this.elections = elections;
    this.answers = answers;
  }
}

class TallyPostBody implements Serializable {
  public String electionsTransactionId;

  TallyPostBody(String electionsTransactionId) {
    this.electionsTransactionId = electionsTransactionId;
  }
}

class VotePostBody implements Serializable {
  public String answer;
  public String electionsTransactionId;

  VotePostBody(String answer, String electionsTransactionId) {
    this.answer = answer;
    this.electionsTransactionId = electionsTransactionId;
  }
}

class SignPostBody implements Serializable {
  public String transactionId;
  public String signature;

  SignPostBody(String transactionId, String signature) {
    this.transactionId = transactionId;
    this.signature = signature;
  }
}
