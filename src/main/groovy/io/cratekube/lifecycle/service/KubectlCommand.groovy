package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.api.DefaultProcessExecutor
import io.cratekube.lifecycle.api.ProcessExecutor

/**
 * Executor for the {@code kubectl} binary.
 *
 * @see ProcessExecutor
 */
class KubectlCommand extends DefaultProcessExecutor {
  String executablePath = '/bin/kubectl'
}
