package pages

object Page {
  trait Component[+E] {
    def element: E
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
            case (param, value) if param.startsWith(":") => Some(param.drop(1) -> pathPart(value))
            case _ => None
          }.toMap)

      val queryParams: Map[String, String] = path.split(Array('?', '&')).toList match {
        case _ :: tail if tail.nonEmpty =>
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
    if (path == "/" && template != "/" || path == "" && template != "") false
    else matchFragments(path, template)
  }

  def pathPart(fullPath: String): String = {
    val queryIndex = fullPath.indexOf("?")
    if (queryIndex == -1) fullPath else fullPath.substring(0, queryIndex)
  }

  def matchFragments(fullPath: String, template: String): Boolean = {
    val pathOnly = pathPart(fullPath)
    val pathSplit = pathOnly.split("/").map(Option(_))
    val templateSplit = template.split("/").map(Option(_))

    def zipped: Array[(Option[String], Option[String])] = pathSplit.zipAll(templateSplit, None, None)

    def allMatch: Boolean = zipped.forall {
      case (Some(pathPart), Some(templatePart)) if pathPart == templatePart || templatePart.startsWith(":") => true
      case (Some(pathPart), Some(templatePart)) if pathPart != templatePart => false
      case (None, _) | (_, None) => false
    }

    pathOnly == template || pathOnly.nonEmpty && template.nonEmpty && allMatch
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
