package pages

object Page {
  trait Component[+E] {
    val element: E
    def init(): Unit = ()
  }

  trait View[E] {
    def location: String
    def register(component: Routing[E])
    def goto(path: String): Unit
  }

  case class RouteContext(params: Map[String, String])

  type RouteResolver[+E] = RouteContext => Component[E]
  type Routing[+E] = PartialFunction[String, Component[E]]

  object RouteContext {
    def from(template: String)(path: String): RouteContext = {
      val params = template
        .split("/")
        .zip(path.split("/")).toSeq.flatMap {
          case (param, value) if param.startsWith(":") => Some(param.drop(1) -> value)
          case _ => None
        }.toMap
      RouteContext(params = params)
    }
  }

  def apply[E](template: String, component: RouteResolver[E]): Routing[E] = {
    case path if matches(path, template) => component(RouteContext.from(template)(path))
  }

  def matches(path: String, template: String): Boolean = path
    .split("/")
    .zipAll(template.split("/"), "none", "none").count(fragmentPair =>
      fragmentPair._1 == fragmentPair._2 ||
        fragmentPair._1.startsWith(":") ||
        fragmentPair._2.startsWith(":")) == path.split("/").length

  def page[E]: (String, RouteResolver[E]) => Routing[E] = apply

  implicit class RoutingOps[E](routing: Routing[E]) {
    def page(template: String, component: RouteResolver[E]): Routing[E] = Page(template, component).orElse(routing)

    def otherwise(component: RouteResolver[E]): Routing[E] = {
      case path: String => routing.applyOrElse(path, (_: String) => component(RouteContext(Map.empty)))
    }

    def view(view: View[E]): Unit = view.register(routing)
  }
}
