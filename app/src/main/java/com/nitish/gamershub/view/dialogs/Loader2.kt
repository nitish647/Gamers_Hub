package com.nitish.gamershub.view.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nitish.gamershub.R
import com.wang.avi.AVLoadingIndicatorView

class Loader2 {
    private  var progressDialog:ProgressDialog?=null
    var dialog: Dialog? = null


    companion object{
     private var loaderInstance : Loader2?=null

       @JvmStatic
      fun instance():Loader2
      {
          if(loaderInstance ==null)

           synchronized(Loader2::class) {
              loaderInstance = Loader2()
          }

          return loaderInstance!!

      }
  }

  fun showLoader(context: Context,title:String)
  {

      progressDialog = ProgressDialog(context)
      progressDialog?.setTitle(title)
      progressDialog?.show()
  }

    fun stopLoader()
    {
        if(progressDialog?.isShowing!! && progressDialog!=null )
        {
            progressDialog?.dismiss()
        }
    }



        fun showCustomDialog(context: Context, title: String?) {

            if (context is Activity && (context as Activity).isFinishing) {
                return
            }

            if (dialog == null) {
                dialog = Dialog(
                    context //, android.R.style.Theme_NoTitleBar
                )
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog?.setContentView(R.layout.loader)
                dialog?.setCancelable(false)
            } else {
                //com.wang.avi.AVLoadingIndicatorView loader = dialog.findViewById ( R.id.avi );
                //loader.hide();
                //dialog.cancel();
            }

            val loader: AVLoadingIndicatorView = dialog!!.findViewById(R.id.avi)
            loader.show()
            val loadertext = dialog!!.findViewById<View>(R.id.message) as TextView
            loadertext.visibility = View.GONE
            loadertext.text = title
            dialog!!.show()
        }

        fun showCustomDialogTransparent(context: Context?, title: String?) {
            if (dialog == null) {
                dialog = Dialog(
                    context!! //, android.R.style.Theme_NoTitleBar
                )
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog?.setContentView(R.layout.loader)
                dialog!!.setCancelable(false)
            } else {
                //com.wang.avi.AVLoadingIndicatorView loader = dialog.findViewById ( R.id.avi );
                //loader.hide();
                //dialog.cancel();
            }
            val loader: com.wang.avi.AVLoadingIndicatorView = dialog!!.findViewById(R.id.avi)
            loader.show()
            val loadertext = dialog!!.findViewById<View>(R.id.message) as TextView
            loadertext.visibility = View.GONE
            loadertext.text = title
            dialog!!.show()
        }

        fun cancelCustomDialog() {
            try {
                if (null != dialog && dialog!!.isShowing) {
                    val loader: com.wang.avi.AVLoadingIndicatorView =
                        dialog!!.findViewById<View>(R.id.avi) as com.wang.avi.AVLoadingIndicatorView
                    //loader.hide();
                    dialog!!.cancel()
                    dialog = null
                    loaderInstance = null
                }
            } catch (e: Exception) {
                dialog = null
                loaderInstance = null
                e.printStackTrace()
            }
        }


}