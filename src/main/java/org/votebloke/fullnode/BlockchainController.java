package org.votebloke.fullnode;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.votebloke.blockchain.StringUtils;
import org.votebloke.blockchain.Transaction;

/** The blockchain controller. */
@RestController
@RequestMapping("/v1/blockchain")
public class BlockchainController {
  private final BlockchainModel chain = new BlockchainModel();

  @GetMapping("/accounts/create")
  public ResponseEntity<String> registerAccount(@RequestHeader("public-key") String base64Key) {
    chain.createAccount(base64Key);
    return new ResponseEntity<String>(
        StringUtils.keyToString(chain.getAccount().getPublicKey()), HttpStatus.OK);
  }

  @GetMapping("/transactions/unsigned")
  public ArrayList<TransactionGetBody> getUnsignedTransactions(
      @RequestParam Optional<String> keyId) {
    return chain.getUnsignedTransactions(keyId.orElse(null));
  }

  @GetMapping("/transactions/")
  @ResponseBody
  public ArrayList<TransactionGetBody> getAllTransactions() {
    return chain.getAllOutputTransactions();
  }

  @GetMapping("/transactions/elections")
  @ResponseBody
  public ArrayList<TransactionGetBody> getOpenElections() {
    return chain.getOpenElections();
  }

  @PostMapping(value = "/transactions/sign", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String signTransaction(@RequestBody PostRequests.SignPostBody body) {
    chain.signTransaction(body.transactionId, body.signature);
    return null;
  }

  @PostMapping(value = "/elections", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String callElections(@RequestBody PostRequests.ElectionsPostBody body) {
    Transaction elections = chain.callElections(body.elections, body.answers);
    return elections.getId();
  }

  @PostMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String castVote(@RequestBody PostRequests.VotePostBody body) {
    Transaction vote = chain.vote(body.answer, body.electionsTransactionId);
    return vote.getId();
  }

  @PostMapping(value = "/tally", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String tallyElections(@RequestBody PostRequests.TallyPostBody body) {
    Transaction tally = chain.tallyElections(body.electionsTransactionId);
    return tally.getId();
  }
}
