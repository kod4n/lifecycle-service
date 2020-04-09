package io.cratekube.lifecycle.model

import groovy.transform.Canonical

/**
 * Represents a CrateKube component.
 */
@Canonical
class Component {
  /** name of this component, for example cluster-mgmt-service */
  String  name
  /** k8s config */
  String config
  /** version of deployed component */
  String currentVersion
  /** latest available version of component */
  String latestVersion
}
