# 자바 스프링 동시성 보고서
---
[1. 동시성 문제란 (#1. 동시성 문제란?)]

[2. 자바 동시성 문제 (#2. 자바 동시성 문제)]

---

## 1. 동시성 문제란?

**동시성 문제**란 여러 쓰레드들이 공유 자원에 대한 경쟁을 벌여 실행 순서에 따라 의도하지 않은 결과를 말한다.

### 동시성 문제 종류

1. **Race Condition** : 두 개 이상의 스레드가 동일한 자원을 동시에 접근하거나 변경하려 할 때, 발생.
   두 스레드가 동일한 변수의 값을 읽고 그 값을 기반으로 수정하려는 경우, 수행하려는 순서에 따라 예기치 않는 결과가 발생한다.
2. **Deadlock** : 두 개 이상의 스레드가 서로 자원이 잠금된 상태에서 대기하는 상황을 의미한다.(순환 대기 또는 무한 대기)
3. **Starvation** : 특정 스레드가 필요한 자원을 영구적으로 확보하지 못해 실행되지 못하는 상태
4. **Livelock** : 스레드가 Deadlock을 피하려고 작업이 멈추지 않고 계속 실행되지만 유의미한 진전 없이 상태만 반복적으로 변경되는 상태

### 동시성 문제의 해결 방법

1. **Lock 메커니즘** : 자원에 대한 접근을 제어하는 가장 기본적인 방법, 한 스레드가 자원을 잠그면 다른 스레드는 해당 자원의 잠금이 해제될 때까지 대기한다.(가장 낮은
   수준의 동기화 메커니즘)
2. **세마포어** : 세마 포어는 제한된 수의 스레드만 자원에 접근할 수 있도록 허용한다. 이는 락보다 유연한 방식으로 여러 스레드가 동시에 자원에 접근할 때 유용하다.
3. **모니터** : 모니터는 락과 유사하지만, 한 번에 하나의 스레드만 해당 객체에 대한 작업을 수행할 수 있게 한다.(더 정교한 동기화 컨트롤이 필요한 경우엔 락을
   사용한다.)

### 락의 종류

1. **Optimistic Locking(낙관적 락)** 은 트랜잭션이 자원에 대한 락을 잡지 않고, 자원에 접근 및 변경 후, 다른 트랜잭션에 의해 변경되지 않았음을 확인하는
   방식이다.
   자원 변경 시점에 충돌이 발생하면 트랜잭션을 다시 수행하는 방식으로 해결한다. 이는 높은 동시성을 제공하지만, 충돌이 빈번하게 발생하면 재시도에 의한 성능 저하가 발생할 수
   있다.

2. **Pessimistic Locking(비관적 락)**은 자원에 접근하기 전 미리 락을 잡아 충돌을 방지하는 방법으로, 동시성이 낮지만 충돌이 자주 발생하는 경우 적합하다.
   자원에 락이
   걸리면 다른 스레드는 해당 자원에 접근할 수 없으며, 성능에 부정적인 영향을 미칠 수 있다.

3. **분산락** 은 여러 시스템이나 인스턴스에서 공유되는 자원에 대해 동시성을 제어하기 위해 사용되는 락 메커니즘. redis, zookeeper, etcd 등을 이용하여
   구현할 수 있다.

---

## 2. 자바 동시성 문제 해결

자바는 멀티 스레드 프로그래밍을 지원하는 언어이다. 그렇기 때문에 동시성 문제가 발생할 수 있고, 크리티컬한 경우에는 공유 자원에 대해 Tread-save 하게 해줘야 한다.

스프링은 Request 하나에 대하여 스레드로 처리하기 때문에 동시성 문제 해결이 필요한 경우가 자주 있다. 때문에 기본적으로 DI 하는 싱글톤 객체를 **무상태** 또는
thread-safe 한 객체를 컴포지트해야 한다.

### 모니터

자바에서는 모든 객체는 monitor를 갖고 있고, monitor는 여러 스레드가 객체로 동시에 접근하는 것을 막는다.

모든 객체는 동기화를 위해 사용되는 모니터 역할을 한다. 스레드가 **synchronized** 블록에 진입하면 해당 객체의 모니터 락을 획득하고, 블록을 나갈 때 이를 해제한다.

따라서 프로그래머가 별도로 락을 생성하지 않아도 synchronized 키워드를 사용하여 동기화할 수 있다.

다만 해당 객체의 락을 거는 개념이기 때문에, 동기화 문제가 발생하지 않을 때도, 동기화가 되기 때문에 성능상에 문제가 있을 수 있으므로 사용하지 않는다.

EX) 과제에서 Synchronized 를 사용하는 경우, 필요없는 다른 사용자의 요청도 모두 동기화 하게 되므로 사용하지 않았다.

syncrhonized를 이용한 컬렉션 : CopyOnWriteArrayList, ConcurrentSkipListMap, ConcurrentHashMap, HashTable

### ReentrantLock

    - 가장 일반적인 lock이며, syncrhonized와는 다르게, 수동으로(명시적으로) lock을 잠그고 해제한다.
    - lock을 얻을 때까지 스레드를 블락시키므로 content switch에 따른 overhead가 발생할 수 있는데, tryLock()을 통한 Lock Polling을
      통해 효율적인 locking 이 가능하다.
    - condition을 사용해서 쓰레드의 종류에 따라 구분된 wating pool에서 따로 기다리도록 하여 선별적인 통지를 가능하게 한다.
    - 재진입아 가능하다. 즉, 동일한 스레드가 이미 락을 획득한 상태에서 다시 같은 락을 요청하면, Deadlock을 피하고 다시 락을 획득할 수 있다. 
    - 생성 시 공정성을 설정할 수 있다. 기본적으로 비공정 모드로 동작하며, 먼저 락을 요청한 스레드가 반드시 먼저 락을 얻는 것이 보장되지 않는다. 공정 모드로 설정하면, 락을 요청한 순서대로 스레드가 락을 획득한다.