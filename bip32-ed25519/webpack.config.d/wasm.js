config.module.rules.push({
    test: /\.wasm$/,
    type: 'webassembly/sync',
});

config.experiments = { asyncWebAssembly: true, syncWebAssembly: true };