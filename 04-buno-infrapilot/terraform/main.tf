terraform {
 required_version=">= 1.5.0"
 required_providers { aws={source="hashicorp/aws",version="~> 5.0"} }
}
provider "aws" { region=var.region }
variable "region" { type=string; default="us-east-1" }
variable "environment" { type=string; default="dev" }
resource "aws_sqs_queue" "approval_events" { name="buno-infrapilot-approvals-${var.environment}" }
output "approval_queue_url" { value=aws_sqs_queue.approval_events.url }
