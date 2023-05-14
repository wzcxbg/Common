package com.sliver.common.arch.mvp

interface IPresenter<V : IView> {
    fun onAttach(view: V)
    fun onDetach()
}