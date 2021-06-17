deviceTypesCommon = require '@resin.io/device-types/common'
{ networkOptions, commonImg, instructions } = deviceTypesCommon

BOARD_PREPARE  = 'Put the NVidia Jetson TX2 NX board in recovery mode'
FLASH_TOOL = 'Unzip BalenaOS image and use the Jetson Flash tool to flash the board. Jetson Flash tool can be found at https://github.com/balena-os/jetson-flash'
DONE_FLASHING  = 'After flashing is completed, please wait until the board is rebooted'

module.exports =
	version: 1
	slug: 'jetson-tx2-nx-devkit'
	name: 'Nvidia Jetson TX2 NX using Xavier NX Devkit'
	arch: 'aarch64'
	state: 'released'

	instructions: [
		BOARD_PREPARE
		FLASH_TOOL
		DONE_FLASHING
	]

	gettingStartedLink:
		windows: 'https://docs.balena.io/jetson-tx2-nx-devkit/nodejs/getting-started/#adding-your-first-device'
		osx: 'https://docs.balena.io/jetson-tx2-nx-devkit/nodejs/getting-started/#adding-your-first-device'
		linux: 'https://docs.balena.io/jetson-tx2-nx-devkit/nodejs/getting-started/#adding-your-first-device'

	supportsBlink: false

	yocto:
		machine: 'jetson-tx2-nx-devkit'
		image: 'balena-image'
		fstype: 'balenaos-img'
		version: 'yocto-dunfell'
		deployArtifact: 'balena-image-jetson-tx2-nx-devkit.balenaos-img'
		compressed: true

	options: [ networkOptions.group ]

	configuration:
		config:
			partition:
				primary: 1
			path: '/config.json'

	initialization: commonImg.initialization
