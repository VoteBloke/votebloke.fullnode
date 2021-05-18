package org.votebloke.fullnode;

class PostRequests {
  static class ElectionsPostBody {
    String elections;
    String[] answers;

    ElectionsPostBody(String elections, String[] answers) {
      this.elections = elections;
      this.answers = answers;
    }
  }

  static class TallyPostBody {
    String electionsTransactionId;

    TallyPostBody(String electionsTransactionId) {
      this.electionsTransactionId = electionsTransactionId;
    }
  }

  static class VotePostBody {
    String answer;
    String electionsTransactionId;

    VotePostBody(String answer, String electionsTransactionId) {
      this.answer = answer;
      this.electionsTransactionId = electionsTransactionId;
    }
  }

  static class SignPostBody {
    String transactionId;
    String signature;

    SignPostBody(String transactionId, String signature) {
      this.transactionId = transactionId;
      this.signature = signature;
    }
  }
}
