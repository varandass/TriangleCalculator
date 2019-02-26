package ua.varandas.trianglecalculator.model

import android.util.Log
import ua.varandas.trianglecalculator.enums.CorrectTriangle
import ua.varandas.trianglecalculator.ext.positive
import kotlin.math.*



// Клас вычисления сторон и углов треугольника!
class Triangle {

    val TAG = "Triangle"
    var A: Float = 100f
    var B: Float = 100f
    var C: Float = 100f

    var uA: Float = 60f
    var uB: Float = 60f
    var uC: Float = 60f

    var P: Float = 300f
    var S: Float = 4330.13f

    var correctTriangle = CorrectTriangle.INCORRECT_DATA


    var pointC = Point()
    var pointA = Point()
    var pointB = Point()


    //Обновление данных треугольника
    fun calculate(): Triangle {

        when {
            //По трем сторонам
            isDataPositive(A, B, C) -> ifIsTriangle { abc() }
            //Две стороны и угол
            isDataPositive(A, B, uC) -> ifIsTriangle { abC() }
            isDataPositive(B, C, uA) -> ifIsTriangle { bcA() }
            isDataPositive(C, A, uB) -> ifIsTriangle { caB() }
            //Сторона и два угла
            isDataPositive(A, uB, uC) -> ifIsTriangle { aBC() }
            isDataPositive(B, uC, uA) -> ifIsTriangle { bCA() }
            isDataPositive(C, uA, uB) -> ifIsTriangle { cAB() }

            //ПРЯМОУГОЛЬНЫЙ ТРЕУГОЛЬНИК
            //Два катета и угол 90
            isDataPositiveAndUngl(C, A, uC) -> ifIsTriangle { caC() }
            isDataPositiveAndUngl(C, B, uC) -> ifIsTriangle { cbC() }
            isDataPositiveAndUngl(B, A, uB) -> ifIsTriangle { baB() }
            isDataPositiveAndUngl(B, C, uB) -> ifIsTriangle { bcB() }
            isDataPositiveAndUngl(A, B, uA) -> ifIsTriangle { abA() }
            isDataPositiveAndUngl(A, C, uA) -> ifIsTriangle { acA() }
            //Катет и противолежащий острый угол и прилежащий угол 90
            isDataPositiveAndUngl(B, uB, uC) -> ifIsTriangle { bBC() }
            isDataPositiveAndUngl(B, uB, uA) -> ifIsTriangle { bBA() }
            isDataPositiveAndUngl(A, uA, uC) -> ifIsTriangle { aAC() }
            isDataPositiveAndUngl(A, uA, uB) -> ifIsTriangle { aAB() }
            isDataPositiveAndUngl(C, uC, uA) -> ifIsTriangle { cCA() }
            isDataPositiveAndUngl(C, uC, uB) -> ifIsTriangle { cCB() }
            //Гипотенуза острый угол и угол 90
            isDataPositiveAndUngl(A,uB,uA) -> ifIsTriangle { aBA() }
            isDataPositiveAndUngl(A,uC,uA) -> ifIsTriangle { aCA() }
            isDataPositiveAndUngl(C,uA,uC) -> ifIsTriangle { cAC() }
            isDataPositiveAndUngl(C,uB,uC) -> ifIsTriangle { cBC() }
            isDataPositiveAndUngl(B,uC,uB) -> ifIsTriangle { bCB() }
            isDataPositiveAndUngl(B,uA,uB) -> ifIsTriangle { bAB() }

            else -> correctTriangle = CorrectTriangle.INCORRECT_DATA
        }

        perimetr()
        square()
        pointA.y = B
        when {
            uC == 90f -> {
                pointB.x = A
                pointB.y = pointC.y
            }
            uA == 90f -> {
                pointB.x = C
                pointB.y = B
            }
            else -> {
                pointB.x = visota()
                pointB.y = coordY()
            }
        }


        Log.d(TAG, "Точка C: x: ${pointC.x} y: ${pointC.y}")
        Log.d(TAG, "Точка A: x: ${pointA.x} y: ${pointA.y}")
        Log.d(TAG, "Точка B: x: ${pointB.x} y: ${pointB.y}")

        return this
    }

    //Является ли фигура треугольником
    private fun isTriangle(): Boolean {
        return A + B > C && A + C > B && B + C > A && uA > 0 && uB > 0 && uC > 0
    }

    //Вычисление координат точки B

    private fun coordY(): Float {

        val visota = visota()
        val res = katetPiphagora(A, visota)
        if (uC > 90) {
            return -res
        }
        return res
    }

    fun visota(): Float {
        val p = P / 2
        val a = A
        val b = B
        val c = C

        return (2 * (sqrt(p * (p - a) * (p - b) * (p - c))) / b)
    }

    private fun abc() {
        uA = cosUgol(B, C, A)
        uB = cosUgol(C, A, B)
        uC = tretiUgol(uA, uB)
    }

    private fun abC() {
        C = dveStoronuUgol(A, B, uC)
        uA = cosUgol(B, C, A)
        uB = tretiUgol(uC, uA)
    }

    private fun bcA() {
        A = dveStoronuUgol(B, C, uA)
        uB = cosUgol(C, A, B)
        uC = tretiUgol(uA, uB)
    }

    private fun caB() {
        B = dveStoronuUgol(C, A, uB)
        uA = cosUgol(B, C, A)
        uC = tretiUgol(uA, uB)
    }

    private fun aBC() {
        uA = tretiUgol(uB, uC)
        B = storonaDvaUgla(A, uB, uA)
        C = storonaDvaUgla(A, uC, uA)

    }

    private fun bCA() {
        uB = tretiUgol(uC, uA)
        A = storonaDvaUgla(B, uA, uB)
        C = storonaDvaUgla(B, uC, uB)
    }

    private fun cAB() {
        uC = tretiUgol(uA, uB)
        A = storonaDvaUgla(C, uA, uC)
        B = storonaDvaUgla(C, uB, uC)
    }

    private fun caC() {
        B = katetPiphagora(C, A)
        uA = cosUgol(B, C, A)
        uB = tretiUgol(uC, uA)
    }

    private fun cbC() {
        A = katetPiphagora(C, B)
        uA = cosUgol(B, C, A)
        uB = tretiUgol(uC, uA)
    }

    private fun baB() {
        C = katetPiphagora(B, A)
        uA = cosUgol(B, C, A)
        uC = tretiUgol(uA, uB)
    }

    private fun bcB() {
        A = katetPiphagora(B, C)
        uA = cosUgol(B, C, A)
        uC = tretiUgol(uA, uB)
    }

    private fun abA() {
        C = katetPiphagora(A, B)
        uB = cosUgol(C, A, B)
        uC = tretiUgol(uA, uB)
    }

    private fun acA() {
        B = katetPiphagora(A, C)
        uB = cosUgol(C, A, B)
        uC = tretiUgol(uA, uB)
    }

    private fun bBC() {
        C = gipotenuzaPiphagora(B, uB)
        A = katetPiphagora(C, B)
        uA = tretiUgol(uB, uC)
    }

    private fun bBA() {
        A = gipotenuzaPiphagora(B, uB)
        C = katetPiphagora(A, B)
        uC = tretiUgol(uB, uA)
    }

    private fun aAC() {
        C = gipotenuzaPiphagora(A, uA)
        B = katetPiphagora(C, A)
        uB = tretiUgol(uC, uA)
    }

    private fun aAB() {
        B = gipotenuzaPiphagora(A, uA)
        C = katetPiphagora(B, A)
        uC = tretiUgol(uB, uA)
    }

    private fun cCA() {
        A = gipotenuzaPiphagora(C, uC)
        B = katetPiphagora(A, C)
        uB = tretiUgol(uC, uA)
    }

    private fun cCB() {
        B = gipotenuzaPiphagora(C, uC)
        A = katetPiphagora(B, C)
        uA = tretiUgol(uC, uB)
    }

    private fun aBA(){
        uC = tretiUgol(uB,uA)
        C = gipotenuzaUgol(A,uC)
        B = gipotenuzaUgol(A,uB)
    }

    private fun aCA(){
        uB = tretiUgol(uC,uA)
        C = gipotenuzaUgol(A,uC)
        B = gipotenuzaUgol(A,uB)
    }

    private fun cAC(){
        uB = tretiUgol(uC,uA)
        A = gipotenuzaUgol(C,uA)
        B = gipotenuzaUgol(C,uB)
    }
    private fun cBC(){
        uA = tretiUgol(uC,uB)
        A = gipotenuzaUgol(C,uA)
        B = gipotenuzaUgol(C,uB)
    }

    private fun bCB(){
        uA = tretiUgol(uC,uB)
        A = gipotenuzaUgol(B,uA)
        C = gipotenuzaUgol(B,uC)
    }
    private fun bAB(){
        uC = tretiUgol(uA,uB)
        A = gipotenuzaUgol(B,uA)
        C = gipotenuzaUgol(B,uC)
    }


    //Является ли обьект треугольником
    private fun ifIsTriangle(lambda: () -> Unit) {
        lambda()
        correctTriangle = if (isTriangle()) {
            CorrectTriangle.IS_TRIANGLE
        } else CorrectTriangle.IS_NOT_TRIANGLE
    }

    //Вычисление периметра
    private fun perimetr() {
        P = A + B + C
    }

    //Вычисление площади
    private fun square() {
        S = sqrt((P / 2) * (P / 2 - A) * (P / 2 - B) * (P / 2 - C))
    }

    //Опредиление положительных данных
    private fun isDataPositive(data1: Float, data2: Float, data3: Float): Boolean {
        return data1.positive() && data2.positive() && data3.positive()
    }

    private fun isDataPositiveAndUngl(gipotenuza: Float, katet: Float, ugol: Float): Boolean {
        return gipotenuza.positive() && katet.positive() && ugol.positive() && (ugol == 90.0f || ugol >= 180)
    }

    //Вычисление по 2м сторонам и углу
    private fun dveStoronuUgol(storona1: Float, storona2: Float, ugol: Float): Float {
        return sqrt(sqr(storona1) + sqr(storona2) - 2 * storona1 * storona2 * cos(toRad(ugol)))
    }

    //Вычисляем по стороне и 2м углам
    private fun storonaDvaUgla(storona: Float, ugol1: Float, ugol2: Float): Float {
        return storona * (sin(toRad(ugol1)) / sin(toRad(ugol2)))

    }

    private fun gipotenuzaPiphagora(katet: Float, ugol: Float): Float {
        return katet / sin(toRad(ugol))
    }

    private fun gipotenuzaUgol(gipotenuza: Float, ugol: Float): Float {
        return gipotenuza * sin(toRad(ugol))
    }

    //Вычисление углов по 3м сторонам
    private fun cosUgol(storona1: Float, storona2: Float, storona3: Float): Float {
        return toDeg(acos((sqr(storona1) + sqr(storona2) - sqr(storona3)) / (2 * storona1 * storona2)))
    }

    //Катет по теореме пифагора
    fun katetPiphagora(gipotenuza: Float, katet: Float): Float {
        return sqrt(sqr(gipotenuza) - sqr(katet))
    }

    //Катет и противоположный острый угол
    //private fun kateUngl(katet: Double, ugol: Double){}

    //Число в квадрате
    private fun sqr(x: Float) = x * x

    //Вычисление третьего угла
    private fun tretiUgol(ugol1: Float, ugol2: Float) = 180 - (ugol1 + ugol2)

    //Градусы в радианы
    private fun toRad(deg: Float) = ((deg * PI) / 180).toFloat()

    //Радианы в градусы
    private fun toDeg(rad: Float) = ((rad * 180) / PI).toFloat()

}