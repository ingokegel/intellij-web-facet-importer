package com.ejt.wfi

import com.intellij.facet.FacetManager
import com.intellij.javaee.web.facet.WebFacet
import com.intellij.javaee.web.facet.WebFacetType
import com.intellij.openapi.externalSystem.service.project.settings.FacetConfigurationImporter
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.vfs.VfsUtilCore

class WebFacetImporter : FacetConfigurationImporter<WebFacet> {
    override fun process(
        module: Module,
        name: String,
        cfg: MutableMap<String, Any>,
        facetManager: FacetManager
    ): Collection<WebFacet> {
        val sourceSet = cfg["sourceSet"]
        val sourceSetModule = ModuleManager.getInstance(module.project).findModuleByName("${module.name}.$sourceSet")
        val sourceSetFacetManager = sourceSetModule?.getComponent(FacetManager::class.java)
        if (sourceSetFacetManager != null) {
            val facet = sourceSetFacetManager.getFacetByType(WebFacetType.getInstance().id)
                ?: sourceSetFacetManager.addFacet(WebFacetType.getInstance(), name, null)
            val webRoots = cfg["webRoots"]
            if (webRoots is Map<*, *>) {
                for ((directoryUrl, relativePath) in webRoots) {
                    if (directoryUrl !is String || relativePath !is String) {
                        continue
                    }
                    val url = VfsUtilCore.pathToUrl(directoryUrl)
                    if (facet.webRoots.none { it.directoryUrl == url }) {
                        facet.addWebRoot(url, relativePath)
                    }
                }
            }
            sourceSetFacetManager.facetConfigurationChanged(facet)
        }
        return emptyList() // return value is not used anyway
    }

    override fun canHandle(typeName: String) = typeName == "web"
}
