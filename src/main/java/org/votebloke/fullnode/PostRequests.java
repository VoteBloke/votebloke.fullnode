package org.votebloke.fullnode;

class ElectionsPostBody {
  String elections;
  String[] answers;

  ElectionsPostBody(String elections, String[] answers) {
    this.elections = elections;
    this.answers = answers;
  }
}

class TallyPostBody {
  String electionsTransactionId;
  String dummy;

  TallyPostBody(String electionsTransactionId, String dummy) {
    this.electionsTransactionId = electionsTransactionId;
    this.dummy = dummy;
  }
}

class VotePostBody {
  String answer;
  String electionsTransactionId;

  VotePostBody(String answer, String electionsTransactionId) {
    this.answer = answer;
    this.electionsTransactionId = electionsTransactionId;
  }
}
