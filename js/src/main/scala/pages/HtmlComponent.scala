package pages

import pages.Page.Component
import scala.xml.Elem

final case class HtmlComponent(element: Elem) extends Component[Elem]
