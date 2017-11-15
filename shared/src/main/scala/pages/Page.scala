package pages

object Page {
  trait Component[+E] {
    val element: E
    def init(): Unit = ()
  }

  trait View[E] {
    def path: String
    def register(component: Routing[E])
    def goto(path: String): Unit
  }

  case class RouteContext(path: String, pathParams: Map[String, String])

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
      RouteContext(path, pathParams = params)
    }
  }

  def apply[E](template: String, component: RouteResolver[E]): Routing[E] = {
    case path if matches(path, template) => component(RouteContext.from(template)(path))
  }

  def matches(path: String, template: String): Boolean = {
    val pathSplit = path.split("/")
    val templateSplit = template.split("/")

    val zippedPartMatchCount = path.split("/").zipAll(templateSplit, ".", ".").count {
      case (pathPart, templatePart) if pathPart == templatePart || templatePart.startsWith(":") => true
      case _ => false
    }

    path == template || path.nonEmpty && template.nonEmpty && pathSplit.length == zippedPartMatchCount
  }

  def page[E]: (String, RouteResolver[E]) => Routing[E] = apply

  implicit class RoutingOps[E](routing: Routing[E]) {
    def page(template: String, component: RouteResolver[E]): Routing[E] = Page(template, component).orElse(routing)

    def otherwise(component: RouteResolver[E]): Routing[E] = {
      case path: String => routing.applyOrElse(path, (_: String) => component(RouteContext(path, pathParams = Map.empty)))
    }

    def view(view: View[E]): Unit = view.register(routing)
  }
}
