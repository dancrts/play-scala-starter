package services.onboarding

import dao.{AccountDAO, WorkspaceDAO}

import javax.inject.{Inject, Singleton}

@Singleton
class OnboardingService @Inject()(workspaceDAO: WorkspaceDAO, accountDAO: AccountDAO) {

}
