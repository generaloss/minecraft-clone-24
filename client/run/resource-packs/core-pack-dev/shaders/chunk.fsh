#version 300 es
precision mediump float;

in vec2 f_texcoord;
in vec4 f_color;
in vec3 f_normal;
in vec3 f_blocklight;
in float f_skylight;

out vec4 fragColor;

uniform sampler2D u_texture;
uniform float u_skylightFactor;

void main() {
    vec4 color = texture(u_texture, f_texcoord) * f_color;
    if(color.a <= 0.0)
        discard;

    float skylight = (f_skylight * u_skylightFactor);
    color.rgb *= max(f_blocklight.rgb, vec3(skylight));
    fragColor = color;
}