package filters

import play.api.Configuration
import play.api.http.HttpFilters

import javax.inject.Inject

class CustomFilters @Inject()(configuration: Configuration,
                              securityHeadersFilter: CustomSecurityHeadersFilter
                             )
  extends HttpFilters {

  val filters = Seq(securityHeadersFilter)
}
