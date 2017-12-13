package pages

import pages.Page.Component
import scala.xml.Elem

case class HtmlComponent(element: Elem) extends Component[Elem]
