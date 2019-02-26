package ua.varandas.trianglecalculator.controller

import ua.varandas.trianglecalculator.interfaces.IMainContract
import ua.varandas.trianglecalculator.model.Triangle

class Controller (private val view: IMainContract.IView): IMainContract.IController{

    override fun calculate(): Triangle {
        return view.setTriangle().calculate()
    }

}