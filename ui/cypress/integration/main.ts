describe('Main Page', () => {

  it('should display page', () => {
    cy.visit('/')
    cy.get('qrbill-root .navbar-button').should('have.text', 'Swiss QR Bill')
  })

  it('should open preview dialog', () => {
    cy.visit('/')
    cy.get('.sticky-footer button').click()
    cy.get('.cdk-overlay-container').should('be.visible')
    cy.get('.preview-section').should('be.visible')
  })

  it('should close the preview', () => {
    cy.get('qrbill-preview button').click()
    cy.get('.cdk-overlay-container').should('not.be.visible')
  })
})
