module.exports = (grunt)->

	grunt.initConfig
		pkg: grunt.file.readJSON('package.json')
		
		coffee:
			client:
				options:
					join: true
				files:
					"src/main/resources/static/scripts/all.js": ["src/main/coffee-script/*.coffee"]

		simplemocha:
			client:
				compilers: 'coffee:coffee-script'
				src: 'test/client/**/*.coffee'

		compass:
			dev:
				options:
					config: 'config.rb'
					force: true
			prod:
				options:
					config: 'config.rb'
					environment: 'production'
					outputStyle: 'compressed'
					force: true

		clean:
			client: ["src/resources/scripts/","src/resources/styles/"]

	grunt.loadNpmTasks 'grunt-contrib-coffee'
	grunt.loadNpmTasks 'grunt-contrib-compass'
	grunt.loadNpmTasks 'grunt-contrib-clean'
	grunt.loadNpmTasks 'grunt-simple-mocha'
	grunt.loadNpmTasks 'grunt-exec'

	grunt.registerTask 'client',['clean:client','compass:dev','simplemocha:client','coffee:client']

	grunt.registerTask 'default',['client']

