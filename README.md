<p align="center">	
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">	
</p>

# Re-issuance Sample CorDapp 
This CorDapp is re-issuance demo and demonstrates how to use re-issuance flows.

Re-issuance CorDapp: https://github.com/corda/reissue-cordapp

## Running the sample

Deploy and run the nodes by:
```
./gradlew deployNodes
./build/nodes/runnodes
```
if you have any questions during setup, please go to https://docs.corda.net/getting-set-up.html for detailed setup instructions.

Once all nodes are started up (Notary, Issuer, Alice & Bob), create a token asset. In Issuer's node shell, run:
```
flow start IssueDemoAppTokens tokenHolderParty: Alice, tokenAmount: 50
```

To list the issued tokens, in Alice's node shell, run:
```
flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```
You should see output similar to the following one:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=<b>50 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=84A3BD0200731762EFEF46F1013DFB47B1067A5C112A3A852CE6E7FF5E51302C(0))]
</pre>

Then, transfer the tokens between Alice and Bob many times to generate transaction back-chain:
<pre>
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 30 
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 20 
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 15
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 25
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 35
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 10
</pre>

Next, list available tokens again:
```
flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```
This time, you should see more than 1 token:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(<b>data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=798D77C4019B3E14FD3F0E47D2BF5EC8DEA107D9B72F4C63C0EA0B854BEB3E16(1)), StateAndRef(state=TransactionState(data=<b>10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=2A386D8B9E91CB50EFE5A2B8A29EEEAA4632B3CBC147E979B9483FC32DAAB370(0))]
</pre>
