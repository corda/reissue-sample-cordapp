<p align="center">	
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">	
</p>

# Re-issuance Sample CorDapp 
This CorDapp is re-issuance demo and demonstrates how to use re-issuance flows.
It also uses Ledger Graph to check transaction back-chain.

Re-issuance CorDapp: https://github.com/corda/reissue-cordapp

Ledger Graph CorDapp: https://github.com/corda/ledger-graph

## Running the sample

### Deploy and run the nodes
Run the following commands:
```
./gradlew deployNodes
./build/nodes/runnodes
```
If you have any questions during setup, please go to https://docs.corda.net/getting-set-up.html for detailed setup 
instructions.

### Generate transaction back-chain
Once all nodes are started up (Notary, Issuer, Alice & Bob), create a token asset:
```
<i>Issuer's node:</i> flow start IssueDemoAppTokens tokenHolderParty: Alice, tokenAmount: 50
<span style="background-color: #FFFF00">Marked text</span>
<mark>Marked text</mark>
```
<span style="background-color: #FFFF00">Marked text</span>
<mark>Marked text</mark>

The flow will return issuance transaction id:
```
Flow completed with result: 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885 `#483d8b`
```


To list the issued tokens, run the following:
```
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```

You should see output similar to the following one:
```
Flow completed with result: [StateAndRef(state=TransactionState(data=50 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885(0))]
```

Now you can check issued token back-chain:
```
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885
```
It should contain only issuance transaction id:
```
Flow completed with result: [9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
```

Then, transfer 30 tokens to Bob:
```
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 30 
```
Note the transaction number is different this time: 
```
Flow completed with result: 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95
```

Now, verify if the transaction was added to the back-chain:
```
<i>Bob's node:</i> flow start GetTransactionBackChain transactionId: 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95
```
Back-chain should contain 2 transaction ids now:
```
Flow completed with result: [0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
```

Then, transfer the tokens between Alice and Bob a few times to make transaction back-chain longer:
```
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 20 
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 15
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 25
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 35
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 10
```

Next, list available tokens again:
```
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```

This time, you should see 2 tokens:
```
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0))]
```

Now check back-chains of both of them:
```
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489
```

The listed transaction ids should be similar. One of them should just contain one more transaction than the other one:
```
Flow completed with result: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD, 45FD8ED4AEFFEB6DED3CB19608EBB55D534DF686A5B1DCC7C528AF29AF359234, 32DBDD8CF3A84DFA55D04E67CA32518F2DFAAE8EA8C08DC77DF3459F14B5D6D4, 8E235472D5D5DB6D83AF9FE05A8C516FF0E86E4180B8207226DD9464C8BD542F, 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
```
```
Flow completed with result: [7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489, 3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD, 45FD8ED4AEFFEB6DED3CB19608EBB55D534DF686A5B1DCC7C528AF29AF359234, 32DBDD8CF3A84DFA55D04E67CA32518F2DFAAE8EA8C08DC77DF3459F14B5D6D4, 8E235472D5D5DB6D83AF9FE05A8C516FF0E86E4180B8207226DD9464C8BD542F, 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
```

### Re-issue tokens

To prune the back-chain, tokens can be re-issued. Start with creating a re-issuance request:
```
<i>Alice's node:</i> flow start RequestDemoAppTokensReIssuanceAndShareRequiredTransactions issuer: Issuer, stateRefStringsToReIssue: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1), 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)]
```

Then issuer has 2 options: accept the request or reject it. We will focus on acceptance first. Run the following 
command to list all re-issuance requests:
```
<i>Issuer's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceRequest
```

There should be exactly one re-issuance request, and the output of the above command should be similar to the following:
```
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceRequest>
      issuer: "O=Issuer, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      stateRefsToReIssue:
      - txhash: "3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD"
        index: 1
      - txhash: "7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489"
        index: 0
      assetIssuanceCommand:
        token:
          issuer: "O=Issuer, L=London, C=GB"
          tokenType:
            tokenIdentifier: "DemoAppToken"
            fractionDigits: 0
        outputs:
        - 0
        - 1
      assetIssuanceSigners:
      - "O=Issuer, L=London, C=GB"
      - "O=Issuer, L=London, C=GB"
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceRequestContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: null
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315"
    index: 0
statesMetadata:
- ref:
    txhash: "57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315"
    index: 0
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceRequest"
  recordedTime: "2020-10-09T16:32:36.114Z"
  consumedTime: null
  status: "UNCONSUMED"
  notary: "O=Notary, L=London, C=GB"
  lockId: null
  lockUpdateTime: null
  relevancyStatus: "RELEVANT"
  constraintInfo:
    constraint:
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
totalStatesAvailable: -1
stateTypes: "UNCONSUMED"
otherResults: []
```

To accept the request and re-issue (locked) state, run:
```
<i>Issuer's node:</i> flow start ReIssueDemoAppTokens reIssuanceRequestRefString: 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315(0)
```

Now list available tokens to make sure new tokens had been re-issued before exiting original states from the vault:
```
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```

You should see both original states and their duplicates. Note that original states are unencumbered, and 
the duplicated states are encumbered.
```
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)), StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=1</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=2</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(1))]
```

Newly generated states, even though they are available, can't be spent without re-issuance lock.
Run the following command to list all re-issuance locks (there should be just one):
```
<i>Alice's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceLock
```

Result: 
```
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceLock>
      issuer: "O=Issuer, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      originalStates:
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0)\
              \ issued by Issuer"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD"
          index: 1
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0)\
              \ issued by Issuer"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489"
          index: 0
      status: "ACTIVE"
      issuerIsRequiredExitTransactionSigner: true
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceLockContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: 0
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04"
    index: 2
statesMetadata:
- ref:
    txhash: "A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04"
    index: 2
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceLock"
  recordedTime: "2020-10-09T16:33:55.450Z"
  consumedTime: null
  status: "UNCONSUMED"
  notary: "O=Notary, L=London, C=GB"
  lockId: null
  lockUpdateTime: null
  relevancyStatus: "RELEVANT"
  constraintInfo:
    constraint:
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
totalStatesAvailable: -1
stateTypes: "UNCONSUMED"
otherResults: []
```

Now it's time to exit the original tokens from the vault:
```
<i>Alice's node:</i> flow start RedeemDemoAppTokens issuer: Issuer, encumbered: null, tokensNum: null, tokenRefsStrings: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1), 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)]
```

The flow will return id of the exit transaction:
```
Flow completed with result: CC306740156BB9442A58FFE9AC5732A64F822568430854F5FF69430769BC6EB5
```

Next, upload exit transaction as an attachment providing its id:
```
<i>Alice's node:</i> flow start UploadTransactionAsAttachment transactionId: CC306740156BB9442A58FFE9AC5732A64F822568430854F5FF69430769BC6EB5
```

The flow will return attachment id:
```
Flow completed with result: E0F43A1509F4F2402590AE383E36D42A946FDEF23BB0863C1953130CC194AAA7
```

Then you can unlock re-issued states, providing their references, re-issuance lock reference, and attachment id of 
token exit transaction:
```
<i>Alice's node:</i> flow start UnlockReIssuedDemoAppStates reIssuedStatesRefStrings: [A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(0), A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(1)], reIssuanceLockRefString: A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(2), deletedStateTransactionHashes: [E0F43A1509F4F2402590AE383E36D42A946FDEF23BB0863C1953130CC194AAA7]
```

Now list tokens one more time:
```
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```
Note that the re-issued states are now unencumbered:
```
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE(1))]
```

Then, list back-chain of the re-issued states:
```
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE
```

The result will contain identifiers of 3 transactions:
- requesting re-issuance
- re-issuance
- unlocking re-issued tokens

```
Flow completed with result: [CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE, A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04, 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315]
```


It's been ideal usecase - tokens has been successfully re-issued. However, it's not the only usecase.
As has been mentioned before, re-issuance request can be rejected by issuer.
// TODO: Reject a request
// TODO: If original state is consumed, delete re-issued states.

<!-- consider showing that encumbered tokens can't be spent and after they are unencumbered - they can -->