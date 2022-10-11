package com.week2.minigame

import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.week2.minigame.databinding.ActivityIngameBinding

class IngameActivity : AppCompatActivity(){

    private lateinit var binding: ActivityIngameBinding
    private lateinit var moles : Array<ImageButton>
    private lateinit var levels : Array<Int>

    // 두더지 상태. 검은색 : 1 / 갈색 : 0 / 토끼 : 2
    var molestate : Array<Int> = arrayOf(0,0,0,0,0,0,0,0,0,0,0,0)
    val range = (0..11)

    var time = 100
    var pause = false
    var score : Int = 0
    var life = 5
    var sleeptime : Long = 0
    var progresstime : Long = 0

    var mediaPlayer: MediaPlayer? = null
    var musicstate = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.bgm)

        val pausebtn = binding.btnIngamePause
        val musicbtn = binding.btnIngameMusic
        val hammerbtn = binding.btnIngameHammer
        val scoretxt = binding.tvIngmaeScore
        val lifetxt = binding.tvIngmaeLife
        val level = binding.ivIngameLevel
        val soundPool = SoundPool.Builder().build()

        val hitsound = soundPool.load(this, R.raw.hitsound,1)
        val levelupsound = soundPool.load(this, R.raw.levelupsound, 1)






        // level drawable array
        levels = arrayOf(R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
            R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten)

        // 두더지 array

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
        

        pausebtn.setOnClickListener {
            val intent = Intent(this, PauseActivity::class.java)
            startActivity(intent)
        }

        musicbtn.setOnClickListener {
            if(musicstate){
                Glide.with(this)
                    .load(R.drawable.soundoff)
                    .into(musicbtn)
                mediaPlayer?.pause()
                musicstate = false
            }else{
                Glide.with(this)
                    .load(R.drawable.soundon)
                    .into(musicbtn)
                mediaPlayer?.start()

                musicstate =  true
            }

        }

        hammerbtn.setOnClickListener {

        }


    }

    override fun onResume() {
        super.onResume()
        pause = false
        gameThread()
        musicstate = true
        mediaPlayer?.start()
    }


    override fun onPause() {
        super.onPause()
        pause = true

        mediaPlayer?.pause()

    }

    
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
            while(life > 0 && time > 0){


            }

            // 게임 Over
            val intent = Intent(this, GameoverActivity::class.java)
            startActivity(intent)

        }.start()

    }


    // 느린 두더지 스레드
    private fun moleThreadLong(){
        Thread(){

            while(!pause){
                
                // 랜덤 인덱스 생성
                var index = range.random()
                
                // 해당 인덱스에 해당하는 두더지상태 변경
                molestate[index] = 0

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

                Thread.sleep(1500 - sleeptime)

            }
        }.start()
    }

    // 중간 두더지 스레드
    private fun moleThreadMedium(){
        Thread(){

            while(!pause){
                var index = range.random()

                molestate[index] = 0

                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.mole)
                        .into(moles[index])
                    moles[index].visibility = View.VISIBLE
                }


                Thread.sleep(1000 - sleeptime)

                runOnUiThread {
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }


                Thread.sleep(1000 - sleeptime)
            }
        }.start()
    }

    
    // 빠른 두더지 스레드
    private fun moleThreadShort(){
        Thread(){

            while(!pause){
                var index = range.random()

                molestate[index] = 0

                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.mole)
                        .into(moles[index])
                    moles[index].visibility = View.VISIBLE
                }


                Thread.sleep(800 - sleeptime)

                runOnUiThread {
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }


                Thread.sleep(800 - sleeptime)
            }
        }.start()
    }

    
    // 검은 두더지 스레드
    private fun moleThreadDark(){
        Thread(){

            while(!pause){
                var index = range.random()

                molestate[index] = 1

                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.blackmole)
                        .into(moles[index])
                    moles[index].visibility = View.VISIBLE
                }


                Thread.sleep(2000- sleeptime)

                runOnUiThread {
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }

                Thread.sleep(2000- sleeptime)
            }
        }.start()
    }

    // 토기 스레드

    private fun rabbitThread(){
        Thread(){

            while(!pause){

                // 랜덤 인덱스 생성
                var index = range.random()

                // 해당 인덱스에 해당하는 몬스터 상태 변경
                molestate[index] = 2

                // 두더지 등장
                runOnUiThread {
                    Glide.with(this)
                        .load(R.drawable.rabbit)
                        .into(moles[index])
                    moles[index].visibility = View.VISIBLE
                }


                Thread.sleep(1500 - sleeptime)

                // 두더지 퇴장
                runOnUiThread {
                    moles[index].visibility = View.INVISIBLE
                    moles[index].isClickable = true
                }

                Thread.sleep(1500 - sleeptime)

            }
        }.start()
    }

    
    // 시간 감소 스레드
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

    
    // 레벨업 모달 스레드
    
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


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

}