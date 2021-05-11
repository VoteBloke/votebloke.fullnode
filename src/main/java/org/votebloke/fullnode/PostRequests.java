package org.votebloke.fullnode;

class ElectionsPostBody {
    String elections;
    String[] answers;

    ElectionsPostBody(String elections, String[] answers) {
        this.elections = elections;
        this.answers = answers;
    }
}
