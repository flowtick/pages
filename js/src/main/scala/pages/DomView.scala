package pages

import org.scalajs.dom
import org.scalajs.dom.{ Event, HashChangeEvent }
import pages.Page.{ Component, Routing, View }

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
class DomView[E](mount: Component[E] => Unit) extends View[E] {
  override def path: String = dom.window.location.hash.replaceFirst("#", "")

  override def register(routing: Routing[E]): Unit = {
    def renderCurrent(): Unit = mount(routing(path))

    dom.window.addEventListener[HashChangeEvent]("hashchange", (_: HashChangeEvent) => renderCurrent())
    dom.window.addEventListener[Event]("load", (_: Event) => renderCurrent())
  }

  override def goto(path: String): Unit = dom.window.location.hash = path
}