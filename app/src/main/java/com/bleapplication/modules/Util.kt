import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bleapplication.R
import com.bleapplication.interfaces.ReplaceFragmentListener


/**
 * Created by Maciej Kozłowski on 08.04.2018.
 */

inline fun <reified T> Fragment.getListenerOrThrowException(java: Class<ReplaceFragmentListener>): T {
    return getListener<T>()
        ?: throw IllegalStateException("Calling class must implement: " + T::class.java.simpleName)
}

inline fun <reified T> Fragment.getListener(): T? {
    var listener = getListenerFromTargetFragment<T>()
    if (listener != null) {
        return listener
    }

    listener = getListenerFromParentFragment<T>()
    if (listener != null) {
        return listener
    }

    return getListenerFromActivity()
}

inline fun <reified T> getListener(target: Any?): T? {
    return if (T::class.java.isInstance(target)) {
        T::class.java.cast(target)
    } else {
        null
    }
}

inline fun <reified T> Fragment.getListenerFromTargetFragment(): T? {
    return getListener(targetFragment)
}

inline fun <reified T> Fragment.getListenerFromParentFragment(): T? {
    return getListener<T>(parentFragment)
}

inline fun <reified T> Fragment.getListenerFromActivity(): T? {
    return getListener<T>(activity)
}


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {

    beginTransaction().func().addToBackStack(null).commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        setCustomAnimations(R.anim.slide_in_left, android.R.anim.fade_out)
        replace(frameId, fragment)
    }
}