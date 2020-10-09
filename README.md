<p align="center">	
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">	
</p>

# Re-issuance Sample CorDapp 
This CorDapp is re-issuance demo and demonstrates how to use re-issuance flows.

Re-issuance CorDapp: https://github.com/corda/reissue-cordapp

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
<pre>
<i>Issuer's node:</i> flow start IssueDemoAppTokens tokenHolderParty: Alice, tokenAmount: 50
</pre>
The flow will return issuance transaction id:
<pre>
Flow completed with result: 96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B
</pre>

To list the issued tokens, run the following:
<pre>
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
</pre>

You should see output similar to the following one:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=50 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B(0))]
</pre>

Now you can check issued token back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B]
</pre>

Then, transfer 30 tokens to Bob:
<pre>
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 30 
</pre>
Note the transaction number is different this time: 
<pre>
Flow completed with result: 6568B9A26100C93D8472340C8D8C83C460C49A815E770D8FF55786E2219D292E
</pre>

Now, verify if the transaction was added to the back-chain:
<pre>
<i>Bob's node:</i> flow start GetTransactionBackChain transactionId: 6568B9A26100C93D8472340C8D8C83C460C49A815E770D8FF55786E2219D292E
</pre>
Back-chain should contain 2 transaction ids now:
<pre>
Flow completed with result: [6568B9A26100C93D8472340C8D8C83C460C49A815E770D8FF55786E2219D292E, 96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B]
</pre>

Then, transfer the tokens between Alice and Bob a few times to make transaction back-chain longer:
<pre>
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 20 
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 15
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 25
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 35
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 10
</pre>

Next, list available tokens again:
<pre>
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
</pre>

This time, you should see 2 tokens:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA(0))]
</pre>

Now check back-chains of both of them:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA
</pre>

The listed transaction ids should be similar. One of them should just contain one moe transaction than the other one:
<pre>
Flow completed with result: [03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F, 3E5529FDF44C5028B73F3B1792AA9C9B8334C09FD3007C641C6BFEE3FD0E8E0F, 6560819C3A6943AF8FD1EB1693CBDC36924E0EA809877DD1397E0E65ADEDBBD1, 633511A15D589BAB75B09F73BB9AD0224F494F1CDB09AD43CCF5789DBE554C33, 6568B9A26100C93D8472340C8D8C83C460C49A815E770D8FF55786E2219D292E, 96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B]
</pre>
<pre>
Flow completed with result: [BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA, 03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F, 3E5529FDF44C5028B73F3B1792AA9C9B8334C09FD3007C641C6BFEE3FD0E8E0F, 6560819C3A6943AF8FD1EB1693CBDC36924E0EA809877DD1397E0E65ADEDBBD1, 633511A15D589BAB75B09F73BB9AD0224F494F1CDB09AD43CCF5789DBE554C33, 6568B9A26100C93D8472340C8D8C83C460C49A815E770D8FF55786E2219D292E, 96AF036406B60A34B2A168521B0B8BDD2C3F18AB7C8EC10EFA7C48008514DB7B]
</pre>

### Re-issue tokens

To prune the back-chain, tokens can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> flow start RequestDemoAppTokensReIssuanceAndShareRequiredTransactions issuer: Issuer, stateRefStringsToReIssue: [03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F(1), BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA(0)]
</pre>

Then issuer has 2 options: accept the request or reject it. We will focus on acceptance first. Run the following 
command to list all re-issuance requests:
<pre>
<i>Issuer's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceRequest
</pre>

There should be exactly one re-issuance request, and the output of the above command should be similar to the following:
<pre>
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceRequest>
      issuer: "O=Issuer, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      stateRefsToReIssue:
      - txhash: "03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F"
        index: 1
      - txhash: "BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA"
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
    txhash: "33880921F3D8006F96D38631C54EB4366B1CFD8053577658EE69EEAEDC309BE9"
    index: 0
statesMetadata:
- ref:
    txhash: "33880921F3D8006F96D38631C54EB4366B1CFD8053577658EE69EEAEDC309BE9"
    index: 0
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceRequest"
  recordedTime: "2020-10-09T12:49:20.082Z"
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
</pre>

To accept the request and re-issue (locked) state, run:
<pre>
flow start ReIssueDemoAppTokens reIssuanceRequestRefString: 33880921F3D8006F96D38631C54EB4366B1CFD8053577658EE69EEAEDC309BE9(0)
</pre>

Now list available tokens to make sure new tokens had been re-issued before exiting original states from the vault:
<pre>
flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
</pre>

You should see duplicates: // TODO: better description
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA(0)), StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=1, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=B6C65D811C5B75529E2CADF1FD8233F4CFF3E419A86BFD12FA59568214FDCC3B(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=2, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=B6C65D811C5B75529E2CADF1FD8233F4CFF3E419A86BFD12FA59568214FDCC3B(1))]
</pre>

Now it's time to exit the original tokens:
flow start RedeemDemoAppTokens issuer: Issuer, encumbered: null, tokensNum: null, tokenRefs: [03B2005AE6EE99CED1E800EF85DEA60219388C16E9F5723B62DE4539B622EC1F(1), BA0228C85B9E34EBF462D5981469A3F17A2380F1C6C34ADFBCE6631C24F74ADA(0)]



// TODO: Reject a request

