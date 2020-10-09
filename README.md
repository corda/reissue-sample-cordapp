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
Flow completed with result: 1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9
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
)), ref=1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9(0))]
</pre>

Now you can check issued token back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9]
</pre>

Then, transfer 30 tokens to Bob:
<pre>
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 30 
</pre>
Note the transaction number is different this time: 
<pre>
Flow completed with result: 2F361C8ACAAC45807988DCB4E5254D4612B6EEECB05FB9F22C525C641DDCF308
</pre>

Now, verify if the transaction was added to the back-chain:
<pre>
<i>Bob's node:</i> flow start GetTransactionBackChain transactionId: 2F361C8ACAAC45807988DCB4E5254D4612B6EEECB05FB9F22C525C641DDCF308
</pre>
Back-chain should contain 2 transaction ids now:
<pre>
Flow completed with result: [2F361C8ACAAC45807988DCB4E5254D4612B6EEECB05FB9F22C525C641DDCF308, 1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9]
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
)), ref=432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3(0))]
</pre>

Now check back-chains of both of them:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3
</pre>

The listed transaction ids should be similar. One of them should just contain one moe transaction than the other one:
<pre>
Flow completed with result: [432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF, FF375E2CA66C220696ACBDA3FA8E807CC417EE07026A99F4E48BC6B4F23BE470, 164299D07DEED4DF5B1568F9D17D019DF3B6B18F69589A051660C0A8661E4FA0, 0130BF4BA9C4E883EFB96C163C8344683970754D4C23947C9428B35D9247619F, 2F361C8ACAAC45807988DCB4E5254D4612B6EEECB05FB9F22C525C641DDCF308, 1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9]
</pre>
<pre>
Flow completed with result: [F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3, 432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF, FF375E2CA66C220696ACBDA3FA8E807CC417EE07026A99F4E48BC6B4F23BE470, 164299D07DEED4DF5B1568F9D17D019DF3B6B18F69589A051660C0A8661E4FA0, 0130BF4BA9C4E883EFB96C163C8344683970754D4C23947C9428B35D9247619F, 2F361C8ACAAC45807988DCB4E5254D4612B6EEECB05FB9F22C525C641DDCF308, 1207737788F900313FB48679A7DDB13121A44DB3C389C14745EC37A0AA53ECF9]
</pre>

### Re-issue tokens

To prune the back-chain, tokens can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> flow start RequestDemoAppTokensReIssuanceAndShareRequiredTransactions issuer: Issuer, stateRefStringsToReIssue: [432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF(1), F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3(0)]
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
      - txhash: "432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF"
        index: 1
      - txhash: "F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3"
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
    txhash: "0609019CE24F3DE00FAE1608B38EE43389C489ED33B6CB33A8576EB846027A4A"
    index: 0
statesMetadata:
- ref:
    txhash: "0609019CE24F3DE00FAE1608B38EE43389C489ED33B6CB33A8576EB846027A4A"
    index: 0
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceRequest"
  recordedTime: "2020-10-09T15:42:27.218Z"
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
<i>Issuer's node:</i> flow start ReIssueDemoAppTokens reIssuanceRequestRefString: 0609019CE24F3DE00FAE1608B38EE43389C489ED33B6CB33A8576EB846027A4A(0)
</pre>

Now list available tokens to make sure new tokens had been re-issued before exiting original states from the vault:
<pre>
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
</pre>

You should see duplicates: // TODO: better description
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3(0)), StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=1, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=2, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0(1))]
</pre>

Now it's time to exit the original tokens:
<pre>
<i>Alice's node:</i> 
flow start RedeemDemoAppTokens issuer: Issuer, encumbered: null, tokensNum: null, tokenRefsStrings: [432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF(1), F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3(0)]
</pre>

Result:
<pre>
Flow completed with result: 3978E182445383B6FA0B3F9A4DAD61339B458B6FAA1AFD02DDCDDBC7D2862D00
</pre>

Upload exit transaction as an attachment:
<pre>
flow start UploadTransactionAsAttachment transactionId: 3978E182445383B6FA0B3F9A4DAD61339B458B6FAA1AFD02DDCDDBC7D2862D00
</pre>

Result:
<pre>
Flow completed with result: 797CCBE574B28B77727CDC6FB8921E4C39FAA2BE37D1FDCB605C370CF9D94600
</pre>

Run the following command to list all re-issuance locks:
<pre>
<i>Alice's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceLock
</pre>

Result: 
<pre>
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
          txhash: "432C5C0557124135BAD970C14DB1DA6CC8DE451BD7F8EE6D703F9D5296C40FEF"
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
          txhash: "F17E1D2BD0B78ED141E1D89B7B2E822D174371D3A3B052C09020703E9D1F32A3"
          index: 0
      status: "ACTIVE"
      issuerIsRequiredExitTransactionSigner: true
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceLockContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: 0
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0"
    index: 2
statesMetadata:
- ref:
    txhash: "EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0"
    index: 2
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceLock"
  recordedTime: "2020-10-09T15:43:53.257Z"
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

Unlock re-issued states:
<pre>
flow start UnlockReIssuedDemoAppStates reIssuedStatesRefStrings: [EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0(0), EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0(1)], reIssuanceLockRefString: EC86F61555C55385DC0EE4982DC5B969AC5BC99E73B7AC6F70C2788D2B7555D0(2), deletedStateTransactionHashes: [797CCBE574B28B77727CDC6FB8921E4C39FAA2BE37D1FDCB605C370CF9D94600]
</pre>

Success!!

// TODO: Reject a request
// TODO: If original state is consumed, delete re-issued states.