#version 330

in vec2 f_texcoord;
in vec4 f_color;
in vec3 f_normal;
in vec3 f_light;

uniform sampler2D u_texture;
uniform vec3 u_lightDirection;

void main() {
    vec4 color = vec4(1.0);//texture(u_texture, f_texcoord) * f_color;
    if(color.a <= 0)
        discard;
    color.rgb *= dot(f_normal, u_lightDirection) * 0.15 + 0.85; // shading
    color.rgb *= f_light.rgb; // light
    gl_FragColor = color;
}