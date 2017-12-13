package pages

import org.scalatest.{ FlatSpec, Matchers }

class HtmlComponentSpec extends FlatSpec with Matchers {
  "HtmlComponent" should "be usable with monadic-xml" in {
    import mhtml.mount
    import scala.xml.Elem
    import org.scalajs.dom.window

    import pages.Page.{ Component, page }

    case class DomComponent(element: Elem) extends Component[Elem]

    val domView = new DomView[Elem](element => {
      val appContainer = window.document.getElementById("body")
      appContainer.innerHTML = ""
      mount(appContainer, element)
    })

    page[Elem]("/page1", _ => DomComponent(<div>This is page 1</div>))
      .page("/page2", context => DomComponent(<div>This is page 2 { context.queryParams.get("foo") }</div>))
      .otherwise(_ => DomComponent(<div>Fallback</div>))
      .view(domView)
  }
}
