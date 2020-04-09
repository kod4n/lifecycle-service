package io.cratekube.lifecycle.job

import de.spinscale.dropwizard.jobs.Job
import de.spinscale.dropwizard.jobs.annotations.Every
import groovy.util.logging.Slf4j
import io.cratekube.lifecycle.AppConfig
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.model.Component
import io.cratekube.lifecycle.modules.annotation.ComponentCache
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require

/**
 * Responsible for polling github to be able to expose component upgrade availability (atom feed). Updates global cache with Component.
 * Uses configuration for components and atom feed location.
 * Current deployed version will come from kubectl call.
 * may need logic to see if components are even deployed - if not only latest version is set
 */
@Slf4j
@Every // see https://github.com/dropwizard-jobs/dropwizard-jobs#configuring-jobs-in-the-dropwizard-config-file
class UpgradeAvailabilityJob extends Job {
  Map<String, Component> componentCache
  KubectlApi kubectlApi
  AppConfig config
  GitHubApi gitHubApi

  @Inject
  UpgradeAvailabilityJob(@ComponentCache Map<String, Component> componentCache, KubectlApi kubectlApi, AppConfig config, GitHubApi gitHubApi) {
    this.componentCache = require componentCache, notNullValue()
    this.kubectlApi = require kubectlApi, notNullValue()
    this.config = require config, notNullValue()
    this.gitHubApi = require gitHubApi, notNullValue()
  }

  /**
   * Needs to check the currently deployed version and the latest from the github atom feed.
   * Use kubectl get calls to find the current deployed version and the atom feed from {@code managedComponents} in app.yml to find the latest available.
   * Every time the service starts this job is executed and the cache is populated. The job repeats on a configurable interval {@code JOB_EXECUTION_FREQUENCY} in app.yml.
   * This information is then used to populate the {@code componentCache} with the populated {@link Component} as the value and the name as the key.
   *
   * if running in a kind cluster we must generate and use an internal kubeconfig because the default is tied to 127.0.0.1
   * kind get kubeconfig --name lifecycle-local --internal > ~/.kube/config-internal
   * kubectl --kubeconfig ~/.kube/config-internal get deployments lifecycle-service-deployment -o json
   *
   * Updates the {@code componentVersionCache} with the retrieved information.
   *
   * {@inheritDoc}
   */
  @Override
  void doJob(JobExecutionContext context) throws JobExecutionException {
    log.debug 'System.currentTimeMillis(): {}', System.currentTimeMillis()
  }
}
