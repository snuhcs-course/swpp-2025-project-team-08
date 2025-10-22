package com.example.itda.data.repository

import android.content.Context
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getMe() : User {
        // TODO - me API (로그인중인 자신 호출) 호출 구현
        //  현재는 dummy에서 받아오도록 구현
        return DummyData.dummyUser[0]
    }

    fun getUser(userId: Int): User {
        // TODO - Program API 호출 구현
        //  현재는 dummy에서 받아오도록 구현
        return DummyData.dummyUser.find { it.id == userId } ?: DummyData.dummyUser[0]
    }

    // TODO - 전체 / 특정 필터링을 거친 Userlist를 불러오는 함수. 현재는 dummyUserList 불러오기
    fun getUserList(): List<User> {
        return DummyData.dummyUser
    }
}