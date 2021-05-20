package org.votebloke.fullnode;

import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;
import org.votebloke.blockchain.StringUtils;
import org.votebloke.blockchain.Transaction;

/** The blockchain controller. */
@RestController
@RequestMapping("/v1/blockchain")
public class BlockchainController {
  private final BlockchainModel chain = new BlockchainModel();

  @GetMapping("/accounts/create")
  public ResponseEntity<?> registerAccount(@RequestHeader("public-key") String base64Key) {
    chain.createAccount(base64Key);
    return new ResponseEntity<>(
        Map.of("public-key", StringUtils.keyToString(chain.getAccount().getPublicKey())),
        HttpStatus.OK);
  }

  @GetMapping("/transactions")
  public ResponseEntity<?> getAllTransactions() {
    return new ResponseEntity<>(chain.getAllOutputTransactions(), HttpStatus.OK);
  }

  @GetMapping("/transactions/unsigned")
  public ResponseEntity<?> getUnsignedTransactions(@RequestParam Optional<String> keyId) {
    return new ResponseEntity<>(chain.getUnsignedTransactions(keyId.orElse(null)), HttpStatus.OK);
  }

  @GetMapping("/transactions/elections")
  public ResponseEntity<?> getOpenElections() {
    return new ResponseEntity<>(chain.getOpenElections(), HttpStatus.OK);
  }

  @PostMapping(value = "/transactions/sign", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> signTransaction(@RequestBody SignPostBody body) {
    chain.signTransaction(body.transactionId, body.signature);
    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping(value = "/elections", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> callElections(
      @RequestHeader("public-key") String base64Key,
      @RequestBody ElectionsPostBody body) {
    chain.createAccount(base64Key);
    Transaction elections = chain.callElections(body.elections, body.answers);
    return new ResponseEntity<>(Map.of("id", elections.getId()), HttpStatus.OK);
  }

  @PostMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> castVote(
      @RequestHeader("public-key") String base64Key, @RequestBody VotePostBody body) {
    chain.createAccount(base64Key);
    Transaction vote = chain.vote(body.answer, body.electionsTransactionId);
    return new ResponseEntity<>(Map.of("id", vote.getId()), HttpStatus.OK);
  }

  @PostMapping(value = "/tally", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> tallyElections(
      @RequestHeader("public-key") String base64Key, @RequestBody TallyPostBody body) {
    chain.createAccount(base64Key);
    Transaction tally = chain.tallyElections(body.electionsTransactionId);
    return new ResponseEntity<>(Map.of("id", tally.getId()), HttpStatus.OK);
  }
}
