import play.api.{ Application, ApplicationLoader }
import play.api.ApplicationLoader.Context

class AppLoader extends ApplicationLoader {
  override def load(context: Context): Application = new AppComponents(context).application
}
