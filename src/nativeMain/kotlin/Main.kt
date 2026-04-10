import com.jakewharton.mosaic.runMosaicMain
import com.suvanl.mybranches.git.RealGitClient
import com.suvanl.mybranches.system.currentUsername
import com.suvanl.mybranches.ui.App
import com.suvanl.mybranches.ui.AppState
import kotlin.system.exitProcess

fun main() {
    val username = try {
        currentUsername()
    } catch (e: IllegalStateException) {
        println("mb: ${e.message}")
        exitProcess(1)
    }

    var finalState: AppState = AppState.Cancelled
    runMosaicMain {
        App(
            gitClient = RealGitClient(),
            username = username,
            onExit = { finalState = it },
        )
    }

    when (val s = finalState) {
        is AppState.Failed -> {
            println("mb: ${s.message}")
            exitProcess(1)
        }

        else -> {}
    }
}
