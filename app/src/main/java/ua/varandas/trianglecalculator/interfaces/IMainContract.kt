package ua.varandas.trianglecalculator.interfaces

import ua.varandas.trianglecalculator.model.Triangle

interface IMainContract {
    interface IController{
        fun calculate(): Triangle
    }
    interface IView{
        fun setTriangle(): Triangle
    }

}