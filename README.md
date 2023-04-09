# 두더지게임 앱

<img src ="https://user-images.githubusercontent.com/86242930/230774847-b678499c-a407-4879-851c-f06df24fd399.jpg" width="400" height="200"/>

## 개인 프로젝트

## 개요

- 스레드 학습을 위한 “두더지잡기” 게임 만들기
- 맡은역할 :  Android 앱개발 Kotlin 사용 / 디자인
- 개발기간 : 2022.10.08 ~ 2022.10.13 (약 5일간)

## 경험

**풀스크린/가로모드** 

→ theme 와 Manifest 파일을 통한 화면세팅

- **멀티스레드 구성**
    
    게임진행 / 몬스터 생성&소멸 / 게임시간 / 레벨업
    
    크게 4가지 스레드를 사용하였다.
    
    ### 1. 게임진행 스레드
    
    가장 큰 줄기의 스레드이며, 게임오버 조건을 판별하기위해 다른 스레드들을 감싸는 형태를 가진다. gameThread가 시작하기 전에, **레벨업을 알리는 레벨업 모달을 숨김처리한 상태로 시작**한다
    
    그후, **모든 스레드들을 동시에 실행시킴과 동시에, 게임오버 조건을 씌운 빈 while문**을 무한히 돌게 된다. 게임 오버 조건 달성시, while문을 탈출하여, GameoverActivity로 이동하게 된다!
    
    ```kotlin
    private fun gameThread(){
        val levelupmodal = binding.ivIngameLevelupmodal
        levelupmodal.isVisible = false
    
        Thread(){
            moleThreadDark()
            moleThreadLong()
            moleThreadMedium()
            moleThreadShort()
            rabbitThread()
            timeThread()
            levelupThread(levelupmodal)
    
            // 게임 종료조건 해당시 while문 탈출
            while(life > 0 && time > 0){ }
    
            // 게임 Over
            val intent = Intent(this, GameoverActivity::class.java)
            startActivity(intent)
    
        }.start()
    
    }
    ```
    
    ### 2. 게임시간 스레드
    
    해당 스레드는, 인게임 화면 상단에 위치한, 타임바에 대한 흐름이다.
    
    두더지를 잡을시 time은 증가하고, 가만히 있으면 time은 감소한다. 따라서 **time 값을 모든 스레드에서 접근 가능한 전역변수로 설정**하고, time스레드에서 progressbar에 띄우는 역할을 한다. 
    
    sleep 을 통해 0.2초마다 한번씩 1의 시간값이 줄어들게 하였고,  이 줄어드는 텀 또한, 레벨에따라 감소하게 하였다
    
    일시정지 버튼 클릭시, 스레드도 멈춰야하므로 while문 조건에 pause boolean 변수와 time 이 > 0일때만 진행되게 하였다
    
    ```kotlin
    private fun timeThread(){
        val timebar = binding.pbIngameTimebar
    
        Thread(){
            while(time > 0 && !pause){
                time -= 1
    
                runOnUiThread {
                    timebar.progress = time
                }
    
                Thread.sleep(200 - progresstime)
            }
    
        }.start()
    }
    ```
    
    ### 3. 레벨업 스레드
    
    내가 커스텀한 레벨업 조건은 score 값이 10,20,40,80,160,320 일때 이다.
    
    따라서, 해당 스레드에서는, score값을 while문을 돌며 계속 확인한후, **레벨업 조건에 부함하는 score값 도달시, levelup modal 창을 화면에 띄우게** 구현하였다
    
    ```kotlin
    private fun levelupThread(levelupmodal : ImageView){
    
        Thread(){
            while(true){
                if(score == 10 || score == 20 || score == 40 || score == 80 || score == 160 || score == 320){
    
                    runOnUiThread {
                        levelupmodal.isVisible = true
                    }
    
                    Thread.sleep(500)
    
                    runOnUiThread {
                        levelupmodal.isVisible = false
                    }
    
                }
            }
        }.start()
    }
    ```
    
    ### 4. 몬스터 생성 & 소멸 스레드
    
    총 5가지의 몬스터들이 랜덤하게 화면에 나타난다. 
    
    느린두더지 / 중간두더지 / 빠른두더지 / 검은두더지 / 토끼
    
    각각 스레드에서 생성 & 소멸 흐름을 컨트롤하게 하였고, **전역변수로 설정해놓은 imageButton 배열로 랜덤한 숫자를 index로 활용하여 배치**할 수 있었다
    
    등장속도와 리젠속도는 Thread.sleep 으로 컨트롤하였다
    
    +) 두더지가 한곳에 곂쳐지는 현상 발생. 
    
    → 이를 방지하기 위해, 크기 12인 Int배열 생성. 구멍이 차있을때 1, 구멍이 비었을때 0 으로 표시. 만약 random 인덱스가 **차있는 구멍에 배정되면, while문을 돌면서 비어있는 구멍 탐색**!!
    
    ```kotlin
    private fun moleThreadLong(){
        Thread(){
    
            while(!pause){
                
                // 랜덤 인덱스 생성
                var index = range.random()
    
    						// 구멍 이미 차있을때, 비어있는 구멍 찾는 로직
                while(holestate[index] == 1){
                    index = range.random()
                }
                
                // 해당 인덱스에 해당하는 두더지상태 변경
                molestate[index] = 0
    
    						// 해당 구멍 차있음 표시
                holestate[index] = 1
    
                // 두더지 등장
                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.mole)
                        .into(moles[index])
                    moles[index].visibility = View.VISIBLE
                }
    
                Thread.sleep(1500 - sleeptime)
    
                // 두더지 퇴장
                runOnUiThread {
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }
    						
    						// 다시 구멍 비었음 표시
    						holestate[index] = 0
    
                Thread.sleep(1500 - sleeptime)
    
            }
        }.start()
    }
    ```
    
- **Time/Score/Level/LifeUI**
    
    모든 ImageButton 들을 ViewBinding으로 호출한뒤, for문으로 클릭 이벤트 처리를 달아주었다. 몬스터를 클릭했을때의 이벤트 처리로, 많은 값들이 변경 또는 유지되어야 되기 때문에, 다소 로직이 복잡해졌다.
    
    **모든 몬스터의 공통사항)**
    
    클릭시 맞는 이미지로 변경된다는것.
    
    **각 몬스터별 다른사항)**
    
    토끼 : time 감소 
    
    갈색두더지 : time 증가 & score 증가 
    
    검은두더지 : life 감소
    
    **score 이 증가되는것을 가장 먼저 알아차리는곳이 click 이벤트 리스너** 이기 때문에, 
    
    score에 따른 
    
    레벨 변화 /  score 증가폭 변화 / 두더지속도 변화 / 시간 감소속도 변화
    
    를 when 조건문을 통해 구현하였다
    
    ```kotlin
    
    // 두더지 ImageButton Array
    moles = arrayOf(binding.btnIngameMole1,
        binding.btnIngameMole2, binding.btnIngameMole3, binding.btnIngameMole4, binding.btnIngameMole5,
        binding.btnIngameMole6, binding.btnIngameMole7, binding.btnIngameMole8, binding.btnIngameMole9,
        binding.btnIngameMole10, binding.btnIngameMole11, binding.btnIngameMole12)
    
    for(i in 0 until 12){
        
        // 모든 두더지가 안보이는상태로 초기화
        moles[i].visibility= View.INVISIBLE
    
        // 두더지 클릭시 이벤트처리
        moles[i].setOnClickListener {
    
            // 클릭시 효과음
            soundPool.play(hitsound, 1.0f, 1.0f, 0,0,1.0f)
    
            // 두번클릭 안되게 방지
            moles[i].isClickable = false
    
            if(molestate[i] == 2){          // 토끼 클릭했을 경우
                Glide.with(this)
                    .load(R.drawable.hitrabbit)
                    .into(moles[i])
                time -= 5
            }
            else if(molestate[i] == 1){      // 검은두더지 클릭했을 경우
                Glide.with(this)
                    .load(R.drawable.hitblackmole)
                    .into(moles[i])
                life--          // 라이프 감소
                lifetxt.text = life.toString()
            }else{                      // 갈색두더지 클릭했을 경우
                Glide.with(this)
                    .load(R.drawable.hitmole)
                    .into(moles[i])
                
                
                // 레벨 구간별 점수 증가폭 증가. 점수 증가
                when(score){
                    in (0..39) -> score++
                    in (40..319) -> score += 2
                }
                scoretxt.text = score.toString()
    
                
                // 타임이 100 아래일때, 타임 증가
                if(time < 100){
                    time += 10
                }
    
                
                // 10/ 20/ 40/ 80/ 160/ 320 점마다 레벨 상승
                // & 두더지 속도 상승
                // & 시간 감소속도 상승
    
                when(score){
                    10 -> {         // 10점일때 : 레벨2
                        Glide.with(this)
                            .load(levels[1])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        progresstime += 30
                    }
                    20 ->{           // 20점일때 : 레벨3
                        Glide.with(this)
                            .load(levels[2])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        progresstime += 30
    
                    }
                    40 ->{          // 40점일때 : 레벨4
                        Glide.with(this)
                            .load(levels[3])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        sleeptime += 200
                        progresstime += 20
    
                    }
                    80 ->{          // 80점일때 : 레벨5
                        Glide.with(this)
                            .load(levels[4])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        sleeptime += 200
                        progresstime += 20
    
                    }
                    160 ->{         // 160점일때 : 레벨6
                        Glide.with(this)
                            .load(levels[5])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        sleeptime += 200
                        progresstime += 20
    
                    }
                    320 ->{         // 320점일때 : 레벨7
                        Glide.with(this)
                            .load(levels[6])
                            .into(level)
                        soundPool.play(levelupsound, 1.0f, 1.0f, 0,0,1.0f)
    
                        progresstime += 20
    
                    }
                }
    
            }
    
        }
    }
    ```
    
- **BGM & 효과음**
    
    
    우선 mp3파일은 어디에 위치시켜야 할까?
    
    → resource 폴더 하위에 raw 폴더를 생성한다. 이곳에 보관한다!!
    
    ### BGM
    
    MediaPlayer 를 이용하여, bgm을 실행, 중단, 릴리스 할 수 있었다.
    
    onCreate에서 정의된 mediaplayer를 onResume에서 실행한다.
    
    onPause 에서  잠시 멈췄다가, onDestroy 에서 릴리즈 한다
    
    ```kotlin
    
    var mediaPlayer: MediaPlayer? = null
    var musicstate = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        mediaPlayer = MediaPlayer.create(this, R.raw.bgm)
    
    		musicbtn.setOnClickListener {
              if(musicstate){
                  mediaPlayer?.pause()
                  musicstate = false
    
              }else{
                  mediaPlayer?.start()
                  musicstate =  true
    
              }
    
          }
    
    }
    
    override fun onResume() {
        super.onResume()
        
        musicstate = true
        mediaPlayer?.start()
    }
    
    override fun onPause() {
        super.onPause()
    
        mediaPlayer?.pause()
    
    }
    
    override fun onDestroy() {
        super.onDestroy()
    
        mediaPlayer?.release()
    }
    ```
    
    ### 효과음
    
    soundpool 를 이용하여, click 이벤트 처리부분에서 사운드 재생하게끔 설정하였다.
    
    ```kotlin
    
    // 선언
    val soundPool = SoundPool.Builder().build()
    
    // 사운드 객체 선언
    val hitsound = soundPool.load(this, R.raw.hitsound,1)
    val levelupsound = soundPool.load(this, R.raw.levelupsound, 1)
    
    // 사운드 객체 재생
    soundPool.play(hitsound, 1.0f, 1.0f, 0,0,1.0f)
    ```
    
- **애니메이션**
    
    **→ TranslateAnimation 객체 활용**
    
    외부 프레임워크를 사용하는 것이 아닌, 애니메이션을 적용시킬 수 있는 자체적인 메소드나 클래스가 있을까 찾아본 결과, TranslateAnimation 객체를 활용하면, 간단한 좌표이동 애니메이션을 구현할 수 있다는 것을 알아내었다.
    
    우선, 최상위 레이아웃에 다음 속성을 추가한다
    
    `android:animateLayoutChanges="true"`
    
    그후, 변수에 TranslateAnimation 객체를 생성하여 담은뒤, 각 속성을 부여해주면 끝나는데,
    
    객체의 생성자는 각각 fromX, toX, fromY, toY 이다.
    
    `val anim = TranslateAnimation(0f,0f,30f,0f)`  이런식으로, 어디서 어디로 오게할건지 컨트롤할 수 있는데, 위 예시는 밑에서 위로 30만큼 올린다는 뜻이다.
    
    생성자로 객체를 생성했으면, 
    
    걸리는시간 / 애니메이션 끝난뒤의 상태 를 설정해준다
    
    `anim.*duration* = 400`
    
    `anim.*fillAfter* = true`
    
    fillAfter 를 true 하게되면, 애니메이션이 끝나도 View가 남아있게되고, false하게되면, 애니메이션 끝나면 View는 없어지게 된다.
    
    그후 마지막으로, visibility로 View의 가시성을 정의해주면 된다!!
    
    ### 1. 레벨업 모달 애니메이션
    
    레벨업을 할때마다, 레벨업 VIew 가 등장해야하는데, 이를 위아래 움직임 애니메이션으로 구현하였다.
    
    ```kotlin
    private fun levelupThread(levelupmodal : ImageView){
    
        Thread{
    
            while(true){
                if(levelupstate){
    
                    runOnUiThread {
    
                        val anim = TranslateAnimation(0f,0f,200f,0f)
                        anim.duration = 400
                        anim.fillAfter = true
                        levelupmodal.animation = anim
                        levelupmodal.visibility = View.VISIBLE
    
                    }
    
                    Thread.sleep(1000)
    
                    runOnUiThread {
                        val anim = TranslateAnimation(0f,0f,0f,levelupmodal.width.toFloat())
                        anim.duration = 400
                        levelupmodal.animation = anim
                        levelupmodal.visibility = View.GONE
                    }
    
                    levelupstate = false
    
                }
            }
        }.start()
    }
    ```
    
    ### 2. 몬스터 등퇴장 애니메이션
    
    두더지가 밑에서 나왔다 들어가는 효과를 주기 위해, 등퇴장 애니메이션을 추가하였다
    
    ```kotlin
    // 느린 두더지 스레드
    private fun moleThreadLong(){
        Thread{
    
            while(!pausestate){
                
                ...
    
                // 두더지 등장
                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.mole)
                        .into(moles[index])
                    val anim = TranslateAnimation(0f,0f,30f,0f)
                    anim.duration = 50
                    moles[index].animation = anim
                    moles[index].visibility = View.VISIBLE
                }
    
                Thread.sleep(1500 - sleeptime)
    
                // 두더지 퇴장
                runOnUiThread {
                    val anim = TranslateAnimation(0f,0f,0f,30f)
                    anim.duration = 50
                    moles[index].animation = anim
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }
    
                ...
    
            }
        }.start()
    }
    ```
    
    https://user-images.githubusercontent.com/86242930/230774930-00cceb6e-a89e-48a1-9b54-09b73b8f4c75.mp4
    

## 게임설명

- 두더지를 클릭하여 점수를얻고, 생존하는 게임
- Life 5 인 상태에서 시작

**→ 3종류의 몬스터 출현** 

- 검정두더지 : 클릭시 라이프감소
- 토끼 : 클릭시 시간 감소
- 두더지 : 클릭시 점수획득 & 시간 증가

**→ 레벨업**

- 10 / 20 / 40 / 80 / 160 / 320 점을 얻을때마다 레벨업.
- 몬스터 속도가 빨라짐
- 시간 감소 속도가 빨라짐

- 레벨 3 까지는 1점씩 증가
- 레벨 7까지는 2점씩 증가

## 구동영상

---

https://user-images.githubusercontent.com/86242930/230774975-e9e82a59-bb8b-434a-8fbe-334bba7a1dfb.mp4

## 스크린샷

---

**→ 시작화면**

<img src ="https://user-images.githubusercontent.com/86242930/230775023-d9f451b2-26c6-490e-89ae-1913bfa23c4b.jpg" width="400" height="200"/>

**→ 인게임 화면**

<p align="left">

    <img src ="https://user-images.githubusercontent.com/86242930/230775032-511b97d2-a973-4c7e-85c1-532ebbf46afc.jpg" width="400" height="200"/>

    <img src ="https://user-images.githubusercontent.com/86242930/230775034-3713448c-0449-422b-8bd2-91893e5eb52a.jpg" width="400" height="200"/>

</p>

**→ 모달창**
<p align="left">

    <img src ="https://user-images.githubusercontent.com/86242930/230775045-5cc1bb0a-77d5-44bf-a071-606f15832a75.jpg" width="400" height="200"/>

    <img src ="https://user-images.githubusercontent.com/86242930/230775049-0d97cb29-b01d-416e-bda5-6d39a660a9cf.jpg" width="400" height="200"/>

</p>
