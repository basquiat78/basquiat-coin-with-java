Simple Basquiat Coin Blockchain with Java Like Bitcoin

# 1. Block
  1.1 Simple Block Complete    
  1.2 Simple Mining API Complete    
  1.3 Block Validation is Proceeding    
  1.4 Block Spec add nonce, difficulty
  1.5 send transaction (send coin)
  1.6 mining block with transaction
  1.7 transaction pool
  
  
# Peer to Peer
	1. websocekt
	2. block 받기
	3. 받은 블록 Validate
	4. replace Block

# Settings
See [application.yml](https://github.com/basquiat78/basquiat-coin-with-java/blob/master/src/main/resources/application.yml)

```
#spring  setup and common configuration
spring:
  profiles:
    active: local
    
---
#spring  setup and common configuration
spring:
  profiles:
    active: local
    
---
#spring profiles : LOCAL
spring:
  profiles: local

# netty server port
tcp:
  port: 8090

# netty server parent thread count
boss:
  thread:
    count: 1

# netty server child thread count
worker:
  thread:
    count: 1

#logging
logging:
  level:
    root: INFO
    org:
      springframework:
        web: DEBUG

# timestamp is 2018-11-21 wednesday
genesis:
  address: PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzo3kBF9AiJysArh8V2GJn6FreBCz7PiDV37BaTyf3tcnZ1UUHJ3EC36YfoTDWp5R79MuBydTrKHbjo7zA9RBX7bi
  hash:
    value: 'Jean-Michel Basquiat die August 12, 1988'
  timestamp: 1542781414755
  difficulty: 0
  nonce: 0

#block file repo setup
#minig block generation interval and difficulty adjustment 15
#mining시에 블록 생성 주기는 15초 (like ethereum)
#minig difficulty 조정은 15블럭마다 조정 
block:
  file:
    name:
      format: .json
      prefix: DOUBLECHAIN_
    path: {your block path}/block/
  generation:
    interval: 15
  difficulty:
    adjustment:
      interval: 15
      
algorithm:
  name: ECDSA
  curve:
    name: secp256k1
  provider: BC
  signature: SHA256withECDSA
  
coinbase:
  amount: 100
  path: {your block path}/wallet/coinbase/
  
  
wallet:
  path: {your block path}/wallet/
  
peer:
  path: {your block path}/peer/  
```


# TEST
1. block정보, wallet과 coinbase정보, peer 목록 정보를 담을 폴더를 생성하고  path에 해당하는 부분을 생성한 폴더의 경로로 변경한다.
2. 구동시 Netty Server를 띄울 포트도 설정한다.
3. postman을 이용해서 wallet을 생성한다.
	POST http://127.0.0.1:8080/addresses {"account": "testUser"}
	--> 2개의 wallet을 생성할 수 있다.
4. 최초로 wallet을 생성시 해당 노드의 코인베이스로 등록되면 여러개의 wallet을 생성한 경우 노드의 코인베이스를 변경해서 마이닝할 수 있다.
   --> 현재 코인베이스 주소가 무엇인지  체크  GET http://127.0.0.1:8080/addresses/coinbase
   --> wallet을 생성할때 넘긴 다른 account로 코인베이스 변경시 GET http://127.0.0.1:8080/addresses/coinbase/{yourotheraccount}
5. mining 
   마이닝시에는 노드의 코인베이스로 보상 코인이 들어간다.
   --> POST http://127.0.0.1:8080/mining/block

6. transaction을 생성하고 마이닝하게 되면 코인을 보낼수 있다.
   6.1 transaction 생성
   --> POST http://127.0.0.1:8080/transactions/sendTransaction {"receivedAddress": "보내고자 할 주소", "amount": 수량}
   --> transaction이 올라갔는지 체크
   	   GET http://127.0.0.1:8080/transactions/transactionPool
   transactionPool이 올라간 상태에서 마이닝을 하게 되면 블록에 삽입되고 코인이 보내고자 하는 주소로 보내진다.

7. balance check
  	   GET http://127.0.0.1:8080/balance/address/{address}
  	   현재 노드의 account로 balance Check
  	   GET http://127.0.0.1:8080/balance/account/{address}
  	   
8. peer
   Eclipse를 사용했기 때문에 해당 프로젝트를 다른 eclipse에서 server port, tcp port, path를 변경해서 띄운다
   e.g 

```
	   	#spring  setup and common configuration
		spring:
		  profiles:
		    active: local
		
		server:
		  port: 9090
		---
		#spring profiles : LOCAL
		spring:
		  profiles: local
		
		# netty server port
		tcp:
		  port: 9091
```

POST http://127.0.0.1:9090/peers {"url": "127.0.0.1", "port": "8090"}

9090에서 처음 띄운 서버의 Netty Server 포트인 8090으로 피어를 붙인다.

block폴더에 8080에서 마이닝한 block 파일들을 불러온다.

9. transaction pool 공유
 서버가 뜨게 되면 15초 간격으로 피어에 transaction pool 정보를 요청하게 된다.
 
 8080에서 sendTransaction을 올리면 9090으로도 공유가 된다.
 GET http://127.0.0.1:9090/transactions/transactionPool
 
# At a glance  
현재 구현된 기능은 심플하게 싱글 노드에서 여러 개의 계정을 생성한다.    
생성한 계정들은 api로 coinbase를 교체할 수 있고 마이닝을 하게 되면 해당 coinbase에 해당하는 주소로 100 코인이 주어지게 된다.
sendTransaction을 통해서 코인을 보낼 주소와 수량을 올려놓고 transactionPool에 올려놨다가 마이닝할때 해당 트랜잭션을 함께 블록 데이터에 넣거나 mineTransaction을 통해서 코인을 보낼 주소와 수량 정보를 올려 마이닝하게 되면 해당 주소로 코인을 보내고 getBalance를 통해 수량을 확인할 수 있다.

블록은 파일로 떨어지며 서버가 꺼졌다 켜지면 블록 정보를 통해 block data와 uTxOs를 초기화 하게 된다.
심플하게 메모리상에 올리는 형식.
  
# Maybe this project will be refactorying....  
현재는 기능을 구현하기에 급급하느라 REST API의 주소 체계가 살짝 맞지 않으며 static으로 대부분 구현해 놨다.    
차후에 utility성과 서비스, 핸들러로 패키지를 다시 나누는 작업이 필요하다	