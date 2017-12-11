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

  case class RouteContext(
    path: String,
    pathParams: Map[String, String],
    queryParams: Map[String, String])

  type RouteResolver[+E] = RouteContext => Component[E]
  type Routing[+E] = PartialFunction[String, Component[E]]

  object RouteContext {
    def from(path: String)(template: Option[String]): RouteContext = {
      val pathParams = template.map(
        _.split("/")
          .zip(path.split("/")).flatMap {
            case (param, value) if param.startsWith(":") => Some(param.drop(1) -> value)
            case _ => None
          }.toMap)

      val queryParams: Map[String, String] = path.split(Array('?', '&')).toList match {
        case (head :: tail) if tail.nonEmpty =>
          tail.flatMap(keyValue => {
            val split = keyValue.trim.split("=")
            val params = for {
              key <- split.headOption
              value <- split.tail.headOption if split.tail.nonEmpty
            } yield (key, value)
            params
          }).toMap
        case _ => Map.empty
      }

      RouteContext(path, pathParams = pathParams.getOrElse(Map.empty), queryParams = queryParams)
    }
  }

  def apply[E](template: String, component: RouteResolver[E]): Routing[E] = {
    case path if matches(path, template) => component(RouteContext.from(path)(Some(template)))
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
      case path: String => routing.applyOrElse(path, (_: String) => component(RouteContext(path, pathParams = Map.empty, queryParams = Map.empty)))
    }

    def view(view: View[E]): Unit = view.register(routing)
  }
}
