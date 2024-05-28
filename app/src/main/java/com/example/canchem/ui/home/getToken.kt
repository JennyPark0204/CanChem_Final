package com.example.canchem.ui.home

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun getToken(context: Context, callback: (String?) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val tokenInFirebase = database.getReference("Token")


    tokenInFirebase.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Token 값 읽어오기
            val accessToken = snapshot.getValue(String::class.java)
            // Callback 함수 호출하여 토큰 값 전달
            callback(accessToken)
        }

        override fun onCancelled(error: DatabaseError) {
            // 오류 처리
            Toast.makeText(context, "토큰을 받아오는 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    })
}
