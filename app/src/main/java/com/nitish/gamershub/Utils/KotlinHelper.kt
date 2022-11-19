package com.nitish.gamershub.Utils

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class KotlinHelper  {
//    var reader = Scanner(System.`in`)
//    var year = reader.nextInt();

    // Main Method Entry Point of Program

}


// program to calculate the marks
fun main(args: Array<String>) {
    // InputStream to get Input
    var reader = Scanner(System.`in`)

    //Input Integer Value






    var subjects = arrayOf("Hindi","English","Physics")

    var marks = IntArray(subjects.size)

    var totalMarks =0;
    for(i in 0..2)
    {
        println("Enter the marks for  : ${subjects[i]} " )
        marks[i] = reader.nextInt()
        totalMarks += marks[i]


    }
    println("total marks = $totalMarks")
    var  percentage = (totalMarks*100)/300
    println("Percentage = $percentage")

    var  grade = if(percentage>=80)
                  {"A"}else { if(percentage>=60) "B" else {"C"} }


    println("Grade = $grade")


}
