package na.distributedGraph.models.queries

case class Query private(search: String) {

    override def toString: String = {
        "QUERY"
    }
}

object Query {

    def apply: Query = new Query("")
}
