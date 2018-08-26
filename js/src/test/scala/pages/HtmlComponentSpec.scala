package pages

import org.scalatest.{ FlatSpec, Matchers }

class HtmlComponentSpec extends FlatSpec with Matchers {
  "HtmlComponent" should "be usable with monadic-xml" in {
    import mhtml.mount
    import scala.xml.Elem
    import org.scalajs.dom.window

    import pages.Page.page

    val domView = new DomView[Elem](component => {
      val appContainer = window.document.getElementById("body")
      appContainer.innerHTML = ""
      mount(appContainer, component.element)
    })

    page[Elem]("/page1", _ => HtmlComponent(<div>This is page 1</div>))
      .page("/page2", context => HtmlComponent(<div>This is page 2 { context.queryParams.get("foo") }</div>))
      .otherwise(_ => HtmlComponent(<div>Fallback</div>))
      .view(domView)
  }
}
