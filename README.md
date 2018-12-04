Simple Basquiat Coin Blockchain with Java Like Bitcoin

# 1. Block
  1.1 Simple Block Complete    
  1.2 Simple Mining API Complete    
  1.3 Block Validation is Complete    
  1.4 PoW (Difficulty and nounce Adjustment)    
  1.5 simple Transaction with uTxO    
  1.6 wallet    
  1.7 transaction pool    
  
  Next Step is peer to peer broadcasting Websocket    
  broadcast for sync block data, transaction pool    
  
현재 구현된 기능은 심플하게 싱글 노드에서 여러 개의 계정을 생성한다.    
생성한 계정들은 api로 coinbase를 교체할 수 있고 마이닝을 하게 되면 해당 coinbase에 해당하는 주소로 100 코인이 주어지게 된다.
sendTransaction을 통해서 코인을 보낼 주소와 수량을 올려놓고 transactionPool에 올려놨다가 마이닝할때 해당 트랜잭션을 함께 블록 데이터에 넣거나 mineTransaction을 통해서 코인을 보낼 주소와 수량 정보를 올려 마이닝하게 되면 해당 주소로 코인을 보내고 getBalance를 통해 수량을 확인할 수 있다.

블록은 파일로 떨어지며 서버가 꺼졌다 켜지면 블록 정보를 통해 block data와 uTxOs를 초기화 하게 된다.
심플하게 메모리상에 올리는 형식.
  
# Maybe this project will be refactorying....  
  