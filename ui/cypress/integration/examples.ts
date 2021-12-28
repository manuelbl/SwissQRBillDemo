describe('Examples Page', () => {

    it('should display examples', () => {
      cy.visit('/examples')
      cy.get('.examples mat-card').should('have.length.greaterThan', 3)
      cy.get('.examples mat-card:nth-child(1)').should('be.visible')
      cy.get('.examples mat-card:nth-child(2)').should('be.visible')
      cy.get('.examples mat-card:nth-child(3)').should('be.visible')
      cy.get('.examples mat-card:nth-child(4)').should('be.visible')
    })

    it('should display images', () => {
        cy.get('.examples mat-card:nth-child(1) img').should('be.visible')
        .should(($imgs) => $imgs.map((_i, img) => expect((img as HTMLImageElement).naturalWidth).to.be.greaterThan(0)));
    })

    it('should navigate to main page', () => {
        cy.get('.examples mat-card:nth-child(2) button').click();
        cy.get('qrbill-root .navbar-button').should('have.text', 'Swiss QR Bill')
    })
  
  })
  