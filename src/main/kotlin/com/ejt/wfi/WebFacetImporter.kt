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
        val targetFacetManager = if (sourceSet != null) {
            ModuleManager.getInstance(module.project)
                .findModuleByName("${module.name}.$sourceSet")
                ?.getComponent(FacetManager::class.java)
        } else {
            facetManager
        }
        if (targetFacetManager != null) {
            val facet = targetFacetManager.getFacetByType(WebFacetType.getInstance().id)
                ?: targetFacetManager.addFacet(WebFacetType.getInstance(), name, null)
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
            targetFacetManager.facetConfigurationChanged(facet)
            return listOf(facet)
        } else {
            return emptyList()
        }
    }

    override fun canHandle(typeName: String) = typeName == "web"
}
