package pages

import org.scalatest.{ FlatSpec, Matchers }
import pages.Page.Component

class PageSpec extends FlatSpec with Matchers {
  case class TestComponent(element: String = "Test Component") extends Component[String]

  "Page" should "resolve single page if path matches" in {
    val component = TestComponent("Hello World!")

    Page.page[String]("/page1", _ => component)("/page1") should be(component)
  }

  it should "fallback to default if no page matches" in {
    val fallback = TestComponent("fallback")

    val routing = Page
      .page[String]("/page1", _ => TestComponent("1"))
      .otherwise(_ => fallback)
      .page("/page2", _ => TestComponent("2"))

    routing.apply("/unknown") should be(fallback)
    routing.apply("/page1") should be(TestComponent("1"))
    routing.apply("/page2") should be(TestComponent("2"))
  }

  it should "throw an error if path does not match and no default defined" in {
    intercept[MatchError] {
      Page
        .page[String]("/page1", _ => TestComponent())
        .page("/page2", _ => TestComponent())
        .apply("/unknown")
    }
  }

  it should "get RouteContext"
}
